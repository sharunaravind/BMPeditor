package Filters;

import Models.Pixel;

import java.io.Serializable;

public class red implements Filters{
    @Override
    public void apply(Pixel p,int Strength) {
        if(p.red + Strength < 255)
        {
            if(p.red+Strength < 0)
            {
                p.red=0;
            }
            else p.red += Strength;
        }
        else p.red = 255;
    }
}
