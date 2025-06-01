package imageProcessors;

import Filters.Filters;
import Kernels.Kernel;
import Models.BmpImage;
import Models.Pixel;
import Models.HSV;
import Filters.HSVfilters;


public class imageProcessor {
    public static void applyFilter(BmpImage image, Filters filter,int Strength)
    {
        int height = image.header.height;
        int width = image.header.width;
        Pixel[][] pixelgrid = image.pixelGrid;
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++) {
               filter.apply(pixelgrid[i][j],Strength);
            }
        }
    }

    public static void convolution(BmpImage image, Kernel kernel)
    {
        int height = image.header.height;
        int width = image.header.width;
        Pixel[][] pixelgrid = image.pixelGrid;
        Pixel[][] newGrid = new Pixel[height][width];
        double[][] matrix = kernel.matrix;
        double divisor = kernel.divisor;
        int offset = kernel.offset;
        int kernelHeight = matrix.length;
        int kernelWidth = matrix[0].length;
        int kernelCenterY = kernelHeight / 2;
        int kernelCenterX = kernelWidth / 2;
        double sumR,sumG,sumB;
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                 sumR = 0.0;
                 sumG = 0.0;
                 sumB = 0.0;

                for (int ky = 0; ky < kernelHeight; ky++)
                {
                    for (int kx = 0; kx < kernelWidth; kx++)
                    {

                        int inputY = i + (ky - kernelCenterY);
                        int inputX = j + (kx - kernelCenterX);

                        if (inputY >= 0 && inputY < height && inputX >= 0 && inputX < width) {
                            Pixel neighborPixel = pixelgrid[inputY][inputX];

                            if (neighborPixel != null) {
                                double weight = matrix[ky][kx];
                                sumR += neighborPixel.red * weight;
                                sumG += neighborPixel.green * weight;
                                sumB += neighborPixel.blue * weight;
                            }
                        }
                    }
                }

                int newR = (int) Math.round((sumR / divisor) + offset);
                int newG = (int) Math.round((sumG / divisor) + offset);
                int newB = (int) Math.round((sumB / divisor) + offset);

                newR = Math.max(0, Math.min(255, newR));
                newG = Math.max(0, Math.min(255, newG));
                newB = Math.max(0, Math.min(255, newB));

                newGrid[i][j] = new Pixel(newR, newG, newB);

            }
        }
        image.pixelGrid = newGrid;
    }

    public static void applyHSVfilter(BmpImage image, HSVfilters filter, int Strength)
    {
        int height = image.header.height;
        int width = image.header.width;
        Pixel[][] pixelgrid = image.pixelGrid;
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++) {

                Pixel currentPixel = pixelgrid[i][j];
                HSV hsvValues = RGBtoHSV(currentPixel);
                filter.apply(hsvValues,Strength);
                Pixel newPixel = HSVtoRGB(hsvValues);
                pixelgrid[i][j] = newPixel;
            }
        }
    }
    public static void rotateleft(BmpImage image)
    {
        int height = image.header.height;
        int width = image.header.width;
        Pixel[][] newGrid = new Pixel[width][height];
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++) {
                newGrid[j][height-1-i] = image.pixelGrid[i][j];
            }
        }
        image.pixelGrid = newGrid;
    }
    public static void rotateright(BmpImage image)
    {
        int height = image.header.height;
        int width = image.header.width;
        Pixel[][] newGrid = new Pixel[width][height];
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++) {
                newGrid[width-1-j][i] = image.pixelGrid[i][j];
            }
        }
        image.pixelGrid = newGrid;
    }

    public static void pixelarise(BmpImage image,int blockSize)
    {
        int height = image.header.height;
        int width = image.header.width;
        Pixel[][] pixelgrid = image.pixelGrid;
        Pixel[][] outputGrid = new Pixel[height][width];
        long sumR=0,sumG=0,sumB=0;
        int count=0,avgR=0,avgG=0,avgB=0;
        for(int i=0;i<height;i+=blockSize)
        {
            for(int j=0;j<width;j+=blockSize)
            {
                sumR = 0;
                sumG = 0;
                sumB = 0;
                count = 0;
                int blockYEnd = Math.min(i + blockSize, height);
                int blockXEnd = Math.min(j + blockSize, width);
                for (int py = i; py < blockYEnd; py++)
                {
                    for (int px = j; px < blockXEnd; px++)
                    {
                        Pixel p = pixelgrid[py][px];
                        if (p != null) {
                            sumR += p.red;
                            sumG += p.green;
                            sumB += p.blue;
                            count++;
                        }
                    }
                }
                avgR=0;
                avgG=0;
                avgB=0;
                if (count > 0) {
                    avgR = (int) Math.round((double) sumR / count);
                    avgG = (int) Math.round((double) sumG / count);
                    avgB = (int) Math.round((double) sumB / count);

                    avgR = Math.max(0, Math.min(255, avgR));
                    avgG = Math.max(0, Math.min(255, avgG));
                    avgB = Math.max(0, Math.min(255, avgB));
                }
                Pixel averagePixel = new Pixel(avgR, avgG, avgB);

                for (int py = i; py < blockYEnd; py++) {
                    for (int px = j; px < blockXEnd; px++) {
                        outputGrid[py][px] = averagePixel;
                    }
                }
            }
        }
        image.pixelGrid = outputGrid;
    }

    private static HSV RGBtoHSV(Pixel pixel) {
        // Normalize R, G, B to 0.0 - 1.0 range
        double r = pixel.red / 255.0;
        double g = pixel.green / 255.0;
        double b = pixel.blue / 255.0;

        double maxC = Math.max(r, Math.max(g, b));
        double minC = Math.min(r, Math.min(g, b));
        double delta = maxC - minC;

        double h = 0; // Default hue
        double s = 0; // Default saturation
        double v = maxC; // Value is the max component

        // Calculate Saturation
        if (maxC > 1e-6) { // Avoid division by zero if max is ~0 (black)
            s = delta / maxC;
        }

        // Calculate Hue (only if color is not grayscale, i.e., delta > 0)
        if (delta > 1e-6) { // Use a small epsilon for float comparison
            if (maxC == r) {
                h = 60 * (((g - b) / delta) % 6);
            } else if (maxC == g) {
                h = 60 * (((b - r) / delta) + 2);
            } else { // maxC == b
                h = 60 * (((r - g) / delta) + 4);
            }
        }

        // Ensure Hue is in [0, 360) range
        if (h < 0) {
            h += 360.0;
        }
        return new HSV(h, s, v);
    }

    private static Pixel HSVtoRGB(HSV hsv) {
        double h = hsv.h;
        double s = Math.max(0.0, Math.min(1.0, hsv.s)); // Clamp S
        double v = Math.max(0.0, Math.min(1.0, hsv.v)); // Clamp V

        double c = v * s; // Chroma
        double hPrime = h / 60.0;
        double x = c * (1 - Math.abs((hPrime % 2) - 1));
        double m = v - c;

        double r1=0, g1=0, b1=0;

        int hSector = ((int)Math.floor(hPrime)) % 6;
        // Correct negative sector calculation if hPrime is negative (though h should be 0-360)
        if (hSector < 0) hSector += 6;


        switch (hSector) {
            case 0: r1 = c; g1 = x; b1 = 0; break;
            case 1: r1 = x; g1 = c; b1 = 0; break;
            case 2: r1 = 0; g1 = c; b1 = x; break;
            case 3: r1 = 0; g1 = x; b1 = c; break;
            case 4: r1 = x; g1 = 0; b1 = c; break;
            case 5: r1 = c; g1 = 0; b1 = x; break;
        }

        int r = (int)Math.round((r1 + m) * 255.0);
        int g = (int)Math.round((g1 + m) * 255.0);
        int b = (int)Math.round((b1 + m) * 255.0);

        // Final clamping
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return new Pixel(r, g, b);
    }


}
