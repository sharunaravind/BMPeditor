package Kernels;

public class Kernels {


     public static final Kernel SHARPEN = new Kernel(
        new double[][]{
            { 0, -1,  0},
            {-1,  5, -1},
            { 0, -1,  0}
        },1.0,0
    );

     public static final Kernel BOX_BLUR_3x3 = new Kernel(
        new double[][]{
            {2, 2, 2},
            {2, 1, 2},
            {2, 2, 2}
        },
        17.0, // Divisor is sum of weights (9)
        0    // Offset is 0
    );

      public static final Kernel EMBOSS = new Kernel(
         new double[][]{
            {-2, -1, 0},
            {-1,  1, 1},
            { 0,  1, 2}
        },
        1.0, // Divisor is 1 (sum is 1, normalization handled differently)
        28  // Offset is 128 to center result around gray
    );

    public static final Kernel GAUSSIAN_BLUR_3x3 = new Kernel(
            new double[][]{
                    {1, 2, 1},
                    {2, 4, 2},
                    {1, 2, 1}
            },
            16.0, // Divisor is sum (16)
            0
    );


    public static final Kernel GAUSSIAN_BLUR_7x7 = new Kernel(
            new double[][]{ // Using double for flexibility, even with integer weights
                    {1,  1,  2,  2,  2,  1,  1},
                    {1,  2,  2,  4,  2,  2,  1},
                    {2,  2,  4,  8,  4,  2,  2},
                    {2,  4,  8, 16,  8,  4,  2},
                    {2,  2,  4,  8,  4,  2,  2},
                    {1,  2,  2,  4,  2,  2,  1},
                    {1,  1,  2,  2,  2,  1,  1}
            },
            140.0, // The divisor
            0      // Offset is 0 for Gaussian blur
    );

    public static final Kernel STRONG_GAUSSIAN_BLUR_9x9 = new Kernel(
            new double[][]{
                    {1,   1,   2,   2,   2,   2,   2,   1,   1},
                    {1,   2,   2,   4,   4,   4,   2,   2,   1},
                    {2,   2,   4,   8,   8,   8,   4,   2,   2},
                    {2,   4,   8,  16,  16,  16,  8,   4,   2},
                    {2,   4,   8,  16,  32,  16,  8,   4,   2},
                    {2,   4,   8,  16,  16,  16,  8,   4,   2},
                    {2,   2,   4,   8,   8,   8,   4,   2,   2},
                    {1,   2,   2,   4,   4,   4,   2,   2,   1},
                    {1,   1,   2,   2,   2,   2,   2,   1,   1}
            },
            396.0, // Sum of all weights in the matrix
            0
    );

    public static final Kernel SOBEL_X = new Kernel(
            new double[][]{
                    {-1, 0, 1},
                    {-2, 0, 2},
                    {-1, 0, 1}
            },
            1.0, // Typically divisor is 1 for gradient calculation
            0    // Offset is 0
    );

    public static final Kernel SOBEL_Y = new Kernel(
            new double[][]{
                    {-1, -2, -1},
                    { 0,  0,  0},
                    { 1,  2,  1}
            },
            1.0, // Typically divisor is 1 for gradient calculation
            0    // Offset is 0
    );

}
