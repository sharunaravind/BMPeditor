package Filters;

import Models.Pixel;

public class posterize implements Filters{

    @Override
    public void apply(Pixel pixel, int Strength) {
        int numLevels = Math.max(2, Strength);
        int levelSize = 256 / numLevels;

        int LevelIndex = pixel.red / levelSize;
        int newColor = LevelIndex * levelSize + levelSize / 2;
        pixel.red = Math.max(0, Math.min(255, newColor));

        LevelIndex = pixel.blue / levelSize;
        newColor = LevelIndex * levelSize + levelSize / 2;
        pixel.blue = Math.max(0, Math.min(255, newColor));

        LevelIndex = pixel.green / levelSize;
        newColor = LevelIndex * levelSize + levelSize / 2;
        pixel.green = Math.max(0, Math.min(255, newColor));

    }
}
