import com.hazelcast.map.IMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplier {
    public static void multiply(IMap<String, int[][]> matrixA,
                                IMap<String, int[][]> matrixB,
                                IMap<String, int[][]> resultMatrix,
                                int numNodes) {
        int[][] A = matrixA.get("A");
        int[][] B = matrixB.get("B");
        int size = A.length;

        int[][] C = new int[size][size];
        ExecutorService executor = Executors.newFixedThreadPool(numNodes);

        for (int i = 0; i < size; i++) {
            final int row = i;
            executor.submit(() -> {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        C[row][j] += A[row][k] * B[k][j];
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all tasks to complete
        }

        resultMatrix.put("C", C); // Store the result in the distributed map
    }
}
