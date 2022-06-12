package ca.fxco.moreculling.utils;

import net.minecraft.client.texture.NativeImage;
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

    public static boolean doesHaveTransparency(NativeImage nativeImage) {
        if (nativeImage.getFormat().hasOpacityChannel()) {
            int width = nativeImage.getWidth();
            int height = nativeImage.getHeight();
            for (int y = 0; y < height; ++y)
                for (int x = 0; x < width; ++x)
                    if (nativeImage.getOpacity(x, y) == 1)
                        return true;
        }
        return false;
    }

    public static boolean doesHaveTranslucency(NativeImage nativeImage) {
        if (nativeImage.getFormat().hasOpacityChannel()) {
            int width = nativeImage.getWidth();
            int height = nativeImage.getHeight();
            for (int y = 0; y < height; ++y)
                for (int x = 0; x < width; ++x)
                    if (nativeImage.getOpacity(x, y) != 0)
                        return true;
        }
        return false;
    }
}
