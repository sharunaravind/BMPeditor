package Filters;

import Models.Pixel;

public class swap implements Filters{
    @Override
    public void apply(Pixel pixel, int Strength) {
        int temp = pixel.red;
        pixel.red = pixel.blue;
        pixel.blue = temp;
    }
}
