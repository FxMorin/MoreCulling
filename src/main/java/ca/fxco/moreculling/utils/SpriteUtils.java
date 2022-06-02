package ca.fxco.moreculling.utils;

import net.minecraft.client.texture.Sprite;

public class SpriteUtils {

    public static boolean doesHaveTransparency(Sprite sprite) {
        int width = sprite.getWidth();
        int height = sprite.getHeight();
        for (int frame : sprite.getDistinctFrameCount().toArray())
            for(int y = 0; y < height; ++y)
                for(int x = 0; x < width; ++x)
                    if (sprite.isPixelTransparent(frame, x, y))
                        return true;
        return false;
    }
}
