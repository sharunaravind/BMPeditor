package Filters;

import Models.HSV;

public class value implements HSVfilters{
    @Override
    public void apply(HSV pixel, int strength)
    {
        double normStrength = strength / 100.0;
        double v_new;
        double v_orig=pixel.v;
        if(normStrength >= 0)
        {
            v_new = v_orig + (1.0 - v_orig) * normStrength;
        }
        else
        {
            v_new = v_orig * (1.0 + normStrength);
        }
        v_new = Math.max(0.0, Math.min(1.0, v_new));
        pixel.v = v_new;
    }
}
