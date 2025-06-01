package Filters;

import Models.Pixel;

public class greyScale implements Filters {
    @Override
    public void apply(Pixel p,int Strength)
    {
        int gray = (int) (0.299 * p.red + 0.587 * p.green + 0.114 * p.blue);
        p.red=gray;
        p.blue=gray;
        p.green=gray;
    }
}

