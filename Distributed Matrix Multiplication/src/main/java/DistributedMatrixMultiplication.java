import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Main class to orchestrate matrix multiplication
public class DistributedMatrixMultiplication {

    public static void main(String[] args) {
        int matrixSize = 1000; // Define your matrix size here.
        int numNodes = 4;      // Simulated number of distributed nodes.

        // Initialize Hazelcast instance
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        // Distributed maps for matrices
        IMap<String, int[][]> matrixA = hazelcastInstance.getMap("matrixA");
        IMap<String, int[][]> matrixB = hazelcastInstance.getMap("matrixB");
        IMap<String, int[][]> resultMatrix = hazelcastInstance.getMap("resultMatrix");

        // Generate large matrices and store them in the distributed map
        MatrixUtils.generateAndStoreMatrix(matrixA, "A", matrixSize);
        MatrixUtils.generateAndStoreMatrix(matrixB, "B", matrixSize);

        // Multiply matrices in a distributed manner
        MatrixMultiplier.multiply(matrixA, matrixB, resultMatrix, numNodes);

        // Display results for validation (small matrix test only)
        if (matrixSize <= 10) {
            MatrixUtils.printMatrix(resultMatrix.get("C"));
        } else {
            System.out.println("Matrix multiplication complete. Results stored in distributed map.");
        }

        // Shutdown Hazelcast instance
        hazelcastInstance.shutdown();
    }
}