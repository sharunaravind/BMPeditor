package Filters;

import Models.Pixel;

public class brightness implements Filters{

    @Override
    public void apply(Pixel pixel, int Strength) {
        if(pixel.red+Strength<255) pixel.red+=Strength;
        else pixel.red=255;
        if(pixel.blue+Strength<255) pixel.blue+=Strength;
        else pixel.blue=255;
        if(pixel.green+Strength<255) pixel.green+=Strength;
        else pixel.green=255;

    }
}
