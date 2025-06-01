package Models;

public class HSV {
    public double h; // Hue (0-360, degrees)
    public double s; // Saturation (0.0-1.0)
    public double v; // Value (0.0-1.0)

    public HSV(double h, double s, double v) {
        this.h = h;
        this.s = s;
        this.v = v;
    }
}
