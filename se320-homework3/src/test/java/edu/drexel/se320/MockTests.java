package edu.drexel.se320;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.util.List;
import java.io.IOException;

public class MockTests {

    public MockTests() {}

    /**
     * Demonstrate a working mock from the Mockito documentation.
     * https://static.javadoc.io/org.mockito/mockito-core/latest/org/mockito/Mockito.html#1
     */
    // WHY I IMPLEMENTED THIS WAY
    /* 
    * I chose to use a constructor injection to pass the mock to the client class.
    * This makes it so that each test case can give the client a mock when it's created.
    * I chose this over a setter injection approach because the client needs the connection from the start
    * and over a factory injection approach because passing the connection directly is more simple.
     */

    @Test
    public void testMockDemo() {
         List<String> mockedList = (List<String>)mock(List.class);

         mockedList.add("one");
         mockedList.clear();

         verify(mockedList).add("one");
         verify(mockedList).clear();
    }

    @Test
    public void testServerConnectionFailureGivesNull() throws IOException {
        // changed code
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo(anyString())).thenReturn(false);

        // If you change the code to pass the mock above to the client (based on your choice of
        // refactoring), this test should pass.  Until then, it will fail.
        assertNull(c.requestFile("DUMMY", "DUMMY"));
    }

    // Test 1: if connection fails, the client should stop
    @Test 
    public void test1() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(false);
        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc).connectTo("server");
        verifyNoMoreInteractions(sc);
    }

    // Test 2: If the file does not exist, the client should close the connection ONCE to ensure connection is closed
    // This test WILL fail since the client does not close the connection when the file does not exist.
    @Test
    public void test2() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(false);
        c.requestFile("server", "DUMMY");
        verify(sc).connectTo("server");
        verify(sc).requestFileContents("DUMMY");
        verify(sc, times(1)).closeConnection();
        verifyNoMoreInteractions(sc);
    }

    // Test 3: For a valid file, the client should read the file's contents. 
    @Test
    public void test3() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true, false);
        when(sc.read()).thenReturn("content");
        c.requestFile("server", "DUMMY");
        verify(sc, atLeastOnce()).read();
    }

    // Test 4: A valid file with no content should return an empty string.
    // moreBytes() will return false because nothing is to be read
    @Test 
    public void test4() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(false);
        String result = c.requestFile("server", "DUMMY");
        assertEquals("", result);
    }

    // Test 5: The first read successfully reads, but the second read throws an IOException.
    // Client should return NULL without any content
    @Test
    public void test5() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true, true, false);
        when(sc.read())
                .thenReturn("partial content")
                .thenThrow(new IOException());

        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc, times(2)).read();
    }

    // Test 6: After connecting successfully, if requesting the file throws an IOException,
    // the client should still close the connection 
    // This test will fail since the client does not close the connection even when requesting the file throws an error.
    @Test
    public void test6() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY"))
                .thenThrow(new IOException("request file failed"));
        c.requestFile("server", "DUMMY");
        verify(sc).requestFileContents("DUMMY");
        verify(sc).closeConnection();
    }

    // Test 7: The client should check moreBytes() before reading file content. 
    // InOrder checks that the methods are called in the correct order.
    @Test
    public void test7() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true, false);
        when(sc.read()).thenReturn("content");
        c.requestFile("server", "DUMMY");
        verify(sc, times(1)).read();
        InOrder order = inOrder(sc);
        order.verify(sc).moreBytes();
        order.verify(sc).read();
    }

    // Test 8: The file should arrive in 4 chunks (1,2,3,4).
    // Client should piece the 4 chunks together in order
    @Test 
    public void test8() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true, true, true, true, false);
        when(sc.read()).thenReturn("1", "2", "3", "4");
        assertEquals(
                "1234", c.requestFile("server", "DUMMY")
        );
        verify(sc, times(4)).read();
    }

    // Test 9: If one chunk is NULL, the client should skip it 
    // instead of adding 'NULL' to the pieced together result
    @Test
    public void test9() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true, true, true, false);
        // The first read returns "1", the second read returns NULL, and the third read returns "4"
        when(sc.read()).thenReturn("1", null, "4");
        assertEquals("14", c.requestFile("server", "DUMMY"));
        verify(sc, times(3)).read();
    }

    // Test 10a: If the first connectTo() call throws an IOException, the client should return NULL
    @Test
    public void test10a() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server"))
                .thenThrow(new IOException("connection failed"));
        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc).connectTo("server");
    }

    // Test 10b: If connection suceeds but the first file request throws an IOException, the client should return NULL
    @Test
    public void test10b() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY"))
                .thenThrow(new IOException("request file failed"));
        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc).requestFileContents("DUMMY");
    }

    // Test 10c: If connection and request file suceeds but the first read throws an IOException,
    // the client should return NULL
    @Test
    public void test10c() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true, false);
        when(sc.read()).thenThrow(new IOException("read file failed"));
        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc).read();
    }

    // Test 10d: If connection and request file suceeds but the first moreBytes() throws an IOException,
    // the client should return NULL
    @Test
    public void test10d() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes())
                .thenThrow(new IOException("checking for bytes failed"));
        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc).moreBytes();
    }

    // Test 10e: If the file is empty and closing the connection throws an IOException, the client should return NULL
    // doThrow() is used to simulate the IOException being thrown when closeConnection() is called
    @Test
    public void test10e() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo("server")).thenReturn(true);
        when(sc.requestFileContents("DUMMY")).thenReturn(true);
        when(sc.moreBytes()).thenReturn(false);
        doThrow(new IOException("closing connection failed"))
                .when(sc).closeConnection();
        assertNull(c.requestFile("server", "DUMMY"));
        verify(sc).closeConnection();
    }
}