import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class DistributedMatrixMultiplicationTest {
    public static void main(String[] args) {
        int[] matrixSizes = {100, 500, 1000, 2000, 4000, 8000, 10000}; // Test different matrix sizes
        int[] nodeCounts = {2, 4, 8};         // Test with different numbers of nodes

        for (int size : matrixSizes) {
            for (int nodes : nodeCounts) {
                System.out.println("Testing with Matrix Size: " + size + ", Nodes: " + nodes);

                HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

                IMap<String, int[][]> matrixA = hazelcastInstance.getMap("matrixA");
                IMap<String, int[][]> matrixB = hazelcastInstance.getMap("matrixB");
                IMap<String, int[][]> resultMatrix = hazelcastInstance.getMap("resultMatrix");

                MatrixUtils.generateAndStoreMatrix(matrixA, "A", size);
                MatrixUtils.generateAndStoreMatrix(matrixB, "B", size);

                long startTime = System.nanoTime();
                MatrixMultiplier.multiply(matrixA, matrixB, resultMatrix, nodes);
                long endTime = System.nanoTime();

                long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
                System.out.println("Execution Time: " + duration + " ms");

                if (size <= 10) {
                    MatrixUtils.printMatrix(resultMatrix.get("C"));
                }

                // Log resource utilization (mocked for now)
                System.out.println("Nodes used: " + nodes);
                System.out.println("Approx. memory per node: " + (size * size * 4 / nodes) + " bytes");

                hazelcastInstance.shutdown();
                System.out.println("\n");
            }
        }
    }
}
