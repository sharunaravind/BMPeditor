package Filters;

import Models.Pixel;

import java.io.Serializable;

public class blue implements Filters{
    @Override
    public void apply(Pixel p,int Strength) {
        if(p.blue + Strength < 255)
        {
            if(p.blue+Strength < 0)
            {
                p.blue=0;
            }
            else p.blue += Strength;
        }
        else p.blue = 255;
    }
}
