package io;

import Models.BmpHeader;
import Models.BmpImage;
import Models.Pixel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BmpWriter {
    public static void write(String outputFilePath, BmpImage image) throws IOException {
        if (image == null || image.header == null || image.pixelGrid == null) {
            throw new IllegalArgumentException("BmpImage or its components cannot be null.");
        }
        BmpHeader header = image.header;
        Pixel[][] pixelGrid = image.pixelGrid;

        int actualHeight = Math.abs(header.height);
        int width = header.width;

        if (header.bitsPerPixel != 24) {
            throw new IllegalArgumentException("Image is not 24 bit. Cant write");
        }


        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFilePath), 65536)) {
            os.write((byte) 'B');
            os.write((byte) 'M');
            writeDword(os, header.fileSize);
            writeWord(os, header.reserved1);
            writeWord(os, header.reserved2);
            writeDword(os, header.pixelOffset);

            writeDword(os, header.headerSize);
            writeDword(os, header.width);
            writeDword(os, header.height);
            writeWord(os, header.planes);
            writeWord(os, header.bitsPerPixel);
            writeDword(os, header.compression);
            writeDword(os, header.imageSize);
            writeDword(os, header.xPixelsPerMeter);
            writeDword(os, header.yPixelsPerMeter);
            writeDword(os, header.colorsInColorTable);
            writeDword(os, header.importantColors);

            int bytesPerPixel = header.bitsPerPixel / 8;
            int rowDataSize = width * bytesPerPixel;
            int padding = (4 - (rowDataSize % 4)) % 4;
            int rowSizeWithPadding = rowDataSize + padding;

            byte[] totalPixelArray = new byte[rowSizeWithPadding * actualHeight];
            int currentBufferIndex = 0;

            for (int y = actualHeight - 1; y >= 0; y--) {
                for (int x = 0; x < width; x++) {
                    Pixel p = pixelGrid[y][x];
                    if (p == null) {
                        totalPixelArray[currentBufferIndex++] = 0;
                        totalPixelArray[currentBufferIndex++] = 0;
                        totalPixelArray[currentBufferIndex++] = 0;
                    } else {
                        totalPixelArray[currentBufferIndex++] = (byte) p.blue;
                        totalPixelArray[currentBufferIndex++] = (byte) p.green;
                        totalPixelArray[currentBufferIndex++] = (byte) p.red;
                    }
                }
                for (int p = 0; p < padding; p++) {
                    totalPixelArray[currentBufferIndex++] = 0;
                }
            }
            os.write(totalPixelArray);

            System.out.println("Pixel data written.");
        }
        System.out.println("BMP file writing complete for: " + outputFilePath);
    }

    public static void writeWord(OutputStream os, int value) throws IOException {
        os.write(value & 0xFF);
        os.write((value >> 8) & 0xFF);
    }

    public static void writeDword(OutputStream os, int value) throws IOException {
        os.write(value & 0xFF);
        os.write((value >> 8) & 0xFF);
        os.write((value >> 16) & 0xFF);
        os.write((value >> 24) & 0xFF);
    }
}