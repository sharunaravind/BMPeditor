package Filters;

import Models.Pixel;

public class darken implements Filters{

    @Override
    public void apply(Pixel pixel, int Strength) {
        if(pixel.red-Strength>0) pixel.red-=Strength;
        else pixel.red=0;
        if(pixel.blue-Strength>0) pixel.blue-=Strength;
        else pixel.blue=0;
        if(pixel.green-Strength>0) pixel.green-=Strength;
        else pixel.green=0;
    }
}
