package Models;

public class BmpHeader {
    public int sigB;        // (1 bytes)
    public int sigM;    // (1 bytes)
    public int fileSize;          //  (4 bytes)
    public int reserved1;   //  (2 bytes)
    public int reserved2;   //  (2 bytes)
    public int pixelOffset;        //  (4 bytes)

    public int headerSize;           //  (4 bytes)
    public int width;             //  (4 bytes)
    public int height;            //  (4 bytes)
    public int planes;            //  (2 bytes)
    public int bitsPerPixel;          //  (2 bytes)
    public int compression;          //  (4 bytes)
    public int imageSize;         //  (4 bytes)
    public int xPixelsPerMeter;      //  (4 bytes)
    public int yPixelsPerMeter;      //  (4 bytes)
    public int colorsInColorTable;        //  (4 bytes)
    public int importantColors;   //  (4 bytes)

    //total size is 54 bytes, 14 for the first one and 40 for the other dunno the names
    public BmpHeader()
    {

    }
    public BmpHeader(int sigB,int sigM, int fileSize, int reserved1, int reserved2, int pixelOffset,
                     int headerSize, int width, int height, int planes, int bitsPerPixel,
                     int compression, int imageSize, int xPixelsPerMeter, int yPixelsPerMeter,
                     int colorsInColorTable, int importantColors)
    {
        this.sigB = sigB;
        this.sigM = sigM;
        this.fileSize = fileSize;
        this.reserved1 = reserved1;
        this.reserved2 = reserved2;
        this.pixelOffset = pixelOffset;

        this.headerSize = headerSize;
        this.width = width;
        this.height = height;
        this.planes = planes;
        this.bitsPerPixel = bitsPerPixel;
        this.compression = compression;
        this.imageSize = imageSize;
        this.xPixelsPerMeter = xPixelsPerMeter;
        this.yPixelsPerMeter = yPixelsPerMeter;
        this.colorsInColorTable = colorsInColorTable;
        this.importantColors = importantColors;
    }

    @Override
    public String toString() {
        return  "Signature: " + (char)sigB + (char)sigM + "\n" +
                "File Size: " + fileSize + "\n" +
                "Reserved1: " + reserved1 + "\n" +
                "Reserved2: " + reserved2 + "\n" +
                "Pixel Offset: " + pixelOffset + "\n" +
                "Header Size: " + headerSize + "\n" +
                "Width: " + width + "\n" +
                "Height: " + height + "\n" +
                "Planes: " + planes + "\n" +
                "Bits Per Pixel: " + bitsPerPixel + "\n" +
                "Compression: " + compression + "\n" +
                "Image Size: " + imageSize + "\n" +
                "X Pixels Per Meter: " + xPixelsPerMeter + "\n" +
                "Y Pixels Per Meter: " + yPixelsPerMeter + "\n" +
                "Colors In Color Table: " + colorsInColorTable + "\n" +
                "Important Colors: " + importantColors;
    }


}
