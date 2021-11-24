package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 *
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int numThreads;

    /**
     * Builds a new MultiThreadedSumMatrix with the given number of threads.
     * @param numThreads The number of threads that will be used to sum the matrix.
     */
    public MultiThreadedSumMatrix(final int numThreads) {
        this.numThreads = numThreads;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startLine;
        private final int numLines;
        private double res;

        /**
         * Builds a new Worker.
         * @param matrix The matrix to sum.
         * @param startLine The initial line for this worker.
         * @param numLines  The number of lines this worker will sum.
         */
        Worker(final double[][] matrix, final int startLine, final int numLines) {
            this.matrix = Arrays.copyOf(matrix, matrix.length);
            this.startLine = startLine;
            this.numLines = numLines;
            this.res = 0;
        }

        @Override
        public void run() {
            System.out.println("Working from line " + this.startLine 
                    + " to line " + (this.startLine + this.numLines - 1));
            final int numRows = this.matrix.length;
            final int numColumns = this.matrix[0].length;
            for (int i = this.startLine; i < numRows && i < this.startLine + this.numLines; i++) {
                for (int j = 0; j < numColumns; j++) {
                    this.res += this.matrix[i][j];
                }
            }
        }

        /**
         * Returns the result of the sum of all lines assigned to the worker.
         * @return the sum of the lines assigned to the worker
         */
        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % numThreads + matrix.length / numThreads;
        // Build a new list of workers
        final List<Worker> workers = new ArrayList<>(numThreads);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        // Start them
        for (final Worker w : workers) {
            w.start();
        }
        // Wait for every one of them to finish
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        // Return the sum
        return sum;
    }

}
