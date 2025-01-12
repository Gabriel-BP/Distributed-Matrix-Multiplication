import hazelcast
import random
import multiprocessing
from time import time

# Utility functions for matrix operations
class MatrixUtils:

    @staticmethod
    def generate_matrix(size):
        return [[random.randint(0, 9) for _ in range(size)] for _ in range(size)]

    @staticmethod
    def print_matrix(matrix):
        for row in matrix:
            print(" ".join(map(str, row)))

# Function to perform matrix multiplication for a single row
def multiply_row(args):
    row, matrix_b, size = args
    result_row = [
        sum(row[k] * matrix_b[k][j] for k in range(size))
        for j in range(size)
    ]
    return result_row

# Class to handle distributed matrix multiplication
class MatrixMultiplier:

    @staticmethod
    def multiply(matrix_a, matrix_b, size, num_processes):
        with multiprocessing.Pool(processes=num_processes) as pool:
            result_matrix = pool.map(
                multiply_row,
                [(matrix_a[i], matrix_b, size) for i in range(size)]
            )
        return result_matrix

# Main function to orchestrate distributed matrix multiplication
def main():
    matrix_size = 1000  # Define your matrix size
    num_nodes = 4       # Number of processes to simulate distributed nodes

    # Hazelcast client setup
    client = hazelcast.HazelcastClient()
    matrix_a_map = client.get_map("matrixA").blocking()
    matrix_b_map = client.get_map("matrixB").blocking()
    result_matrix_map = client.get_map("resultMatrix").blocking()

    # Generate and store matrices in distributed map
    matrix_a = MatrixUtils.generate_matrix(matrix_size)
    matrix_b = MatrixUtils.generate_matrix(matrix_size)
    matrix_a_map.put("A", matrix_a)
    matrix_b_map.put("B", matrix_b)

    # Start matrix multiplication
    start_time = time()
    result_matrix = MatrixMultiplier.multiply(matrix_a, matrix_b, matrix_size, num_nodes)
    end_time = time()

    # Store result in distributed map
    result_matrix_map.put("C", result_matrix)

    # Output results for small matrices
    if matrix_size <= 10:
        MatrixUtils.print_matrix(result_matrix)
    print(f"Matrix multiplication complete. Execution time: {end_time - start_time:.2f} seconds")

    # Shutdown Hazelcast client
    client.shutdown()

if __name__ == "__main__":
    main()
