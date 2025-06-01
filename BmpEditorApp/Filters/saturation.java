package Filters;

import Models.HSV;

public class saturation implements HSVfilters{
    @Override
    public void apply(HSV pixel, int strength) {
        double normStrength = strength / 100.0;
        double s_new;
        double s_orig=pixel.s;
        if(normStrength >= 0)
        {
            s_new = s_orig + (1.0 - s_orig) * normStrength;
        }
        else
        {
            s_new = s_orig * (1.0 + normStrength);
        }
        s_new = Math.max(0.0, Math.min(1.0, s_new));
        pixel.s = s_new;
    }
}
