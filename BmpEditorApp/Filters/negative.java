package Filters;

import Models.Pixel;

public class negative implements Filters{
    @Override
    public void apply(Pixel pixel, int Strength) {
        pixel.red=255-pixel.red;
        pixel.blue=255-pixel.blue;
        pixel.green=255-pixel.green;
    }
}
