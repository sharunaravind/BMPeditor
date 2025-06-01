package Filters;

import Models.Pixel;

public class threshold implements Filters {
    @Override
    public void apply(Pixel p,int thresh)
    {
        int brightness = (int) (0.299 * p.red + 0.587 * p.green + 0.114 * p.blue);
        if(brightness<thresh)
        {
            p.red=10;
            p.blue=10;
            p.green=10;
        }
        else {
            p.red=235;
            p.blue=235;
            p.green=235;
        }
    }
}