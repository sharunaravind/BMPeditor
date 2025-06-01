package Filters;

import Models.Pixel;

public class solarize implements Filters{
    @Override
    public void apply(Pixel pixel, int Strength) {
        if(pixel.red>Strength) pixel.red=255-pixel.red;
        if(pixel.blue>Strength) pixel.blue=255-pixel.blue;
        if(pixel.green>Strength) pixel.green=255-pixel.green;
    }
}
