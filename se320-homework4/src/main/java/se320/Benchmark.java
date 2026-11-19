package se320;

import java.util.Random;

public class Benchmark {

    public static final int OPS = 1000000;

    public static Random rand = new Random();

    public static final int ITERATIONS = 10;

    public static void main(String[] args) throws java.io.IOException {
        // logging
        //int reads = 5;
        //int inserts = 90;
        //int deletes = 5;
        
        // read heavy
        //int reads = 90;
        //int inserts = 5;
        //int deletes = 5;
        
        
        // read only
        int reads = 100;
        int inserts = 0;
        int deletes = 0;
        
        System.out.println("Attaching profiler: press Enter");
        System.in.read();
        long warmupStart = System.nanoTime();
        runOps(reads, inserts, deletes);
        long warmupElapsed = System.nanoTime() - warmupStart;
        System.out.println("Warmup round:    "+String.format("%12s",warmupElapsed)+"ns");

        long[] elapsed = new long[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();
            runOps(reads, inserts, deletes);
            long estimated = System.nanoTime() - startTime;
            elapsed[i] = estimated;
        }

        /* Print the times, as well as the max, min, and average */
        long total = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < 10; i++) {
            System.out.println("Benchmark run "+i+": "+String.format("%12s",elapsed[i])+"ns");
            total += elapsed[i];
            if (elapsed[i] < min) min = elapsed[i];
            if (elapsed[i] > max) max = elapsed[i];
        }
        System.out.println("Average for "+OPS+" operations: "+String.format("%12s",(total/10))+"ns");
        System.out.println("Minimum for "+OPS+" operations: "+String.format("%12s",min)+"ns");
        System.out.println("Maximum for "+OPS+" operations: "+String.format("%12s",max)+"ns");
    }

    public static void runOps(int reads, int inserts, int deletes) {
        RedBlackBST<Integer,Integer> tree = new RedBlackBST<Integer,Integer>();
        int insert_bound = reads + inserts;

        int lastn = 0;

        for(int i = 0; i < OPS; i++) {
            int v = rand.nextInt(100);
            if (v < reads) {
                // do read
                tree.get(lastn);
            } else if (v < insert_bound) {
                // do insertion of random key
                tree.put(lastn, lastn);
            } else {
                // do deletion of random key
                tree.delete(lastn);
            }
            lastn = v;
        }
    }
}




