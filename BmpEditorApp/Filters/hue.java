package Filters;

import Models.HSV;

public class hue implements HSVfilters{

    @Override
    public void apply(HSV pixel, int strength) {
        pixel.h = (pixel.h+strength) % 360;
    }
}
