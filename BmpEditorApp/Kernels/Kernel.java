package Kernels;

public class Kernel {
    public final double[][] matrix;
    public final double divisor;
    public final int offset;

    // Constructor
    public Kernel(double[][] matrix, double divisor, int offset) {

        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Kernel matrix cannot be null or empty.");
        }
        if (matrix.length % 2 == 0 || matrix[0].length % 2 == 0) {
            System.err.println("Warning: Kernel dimensions should ideally be odd (e.g., 3x3, 5x5).");
        }

        this.matrix = matrix;
        this.divisor = divisor;
        this.offset = offset;
    }
}
