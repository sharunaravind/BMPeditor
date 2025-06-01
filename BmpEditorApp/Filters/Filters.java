package Filters;

import Models.BmpImage;
import Models.HSV;
import Models.Pixel;

public interface Filters {

    void apply(Pixel pixel,int Strength);
}
