import com.hazelcast.map.IMap;
import java.util.Random;

public class MatrixUtils {
    public static void generateAndStoreMatrix(IMap<String, int[][]> map, String key, int size) {
        map.put(key, generateMatrix(size));
    }

    public static int[][] generateMatrix(int size) {
        Random random = new Random();
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(10); // Random values between 0 and 9
            }
        }
        return matrix;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}
