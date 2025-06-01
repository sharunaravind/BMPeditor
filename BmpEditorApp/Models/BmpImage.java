package Models;

public class BmpImage {
    //has both header and the pixelGrid
    public BmpHeader header;
    public Pixel[][] pixelGrid;

    public BmpImage(BmpHeader header,int width, int height)
    {
        this.header=header;
        this.pixelGrid = new Pixel[height][width];
    }
    public BmpImage()
    {
        this.header = new BmpHeader();
        this.pixelGrid = new Pixel[header.height][header.width];
    }
    public void UpdateHeader(int FirstSize, int SecondSize)
    {
        this.header.headerSize=SecondSize;
        this.header.pixelOffset= FirstSize +SecondSize;
        int finalHeight = this.pixelGrid.length;
        int finalWidth = this.pixelGrid[0].length;
        this.header.width = finalWidth;
        this.header.height = finalHeight;
        int rowDataSize = finalWidth * 3; // 3 bytes per pixel for 24-bit
        int padding = (4 - (rowDataSize % 4)) % 4;
        int stride = rowDataSize + padding;
        this.header.imageSize = stride * finalHeight;
        this.header.fileSize = FirstSize+SecondSize + this.header.imageSize;
    }
}
