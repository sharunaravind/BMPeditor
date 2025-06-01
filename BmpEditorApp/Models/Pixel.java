package Models;

public class Pixel {
    public int red;
    public int green;
    public int blue;
    int alpha;

    public Pixel(int r, int g, int b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }
    public Pixel(int r, int g, int b,int a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha=a;
    }

    public String toString() {
        return "Pixel(R=" + red + ", G=" + green + ", B=" + blue + "Alpha =" + alpha+ ")";
    }
}