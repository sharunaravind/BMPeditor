package Filters;

import Models.Pixel;

public class solarizeTest implements Filters{
    @Override
    public void apply(Pixel pixel, int Strength) {
        if(pixel.red>Strength && pixel.blue>Strength && pixel.green>Strength)
        {
                pixel.red=255-pixel.red;
                pixel.blue=255-pixel.blue;
                pixel.green=255-pixel.green;
        }
    }
}
