package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        if (nativeImage.getFormat().hasAlpha()) {
            int width = nativeImage.getWidth();
            for (int y = 0; y < nativeImage.getHeight(); ++y)
                for (int x = 0; x < width; ++x)
                    if (nativeImage.getOpacity(x, y) == 0)
                        return true;
        }
        return false;
    }

    public static boolean doesHaveTranslucency(NativeImage image, @Nullable List<NativeImage> orMatch) {
        if (image.getFormat().hasAlpha()) {
            int width = image.getWidth();
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < width; ++x) {
                    if (image.getOpacity(x, y) != -1) { //translucent
                        if (orMatch != null) {
                            boolean success = false;
                            for (NativeImage nativeImage : orMatch) {
                                if (x <= nativeImage.getWidth() && y <= nativeImage.getHeight() &&
                                        nativeImage.getOpacity(x, y) == -1) { // not translucent
                                    success = true;
                                    break;
                                }
                            }
                            if (success) {
                                continue; // Found a none translucent pixel
                            }
                            return true;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void printOpacity(Sprite sprite) {
        printOpacity(null, sprite);
    }

    public static void printOpacity(@Nullable String id, Sprite sprite) {
        NativeImage img = ((SpriteOpacity) sprite).getUnmipmappedImage();
        if (img.getFormat().hasOpacityChannel()) {
            printOpacity(img);
        }
    }

    public static void printOpacity(NativeImage nativeImage) {
        if (!nativeImage.getFormat().hasOpacityChannel()) return;
        int width = nativeImage.getWidth();
        for (int y = 0; y < nativeImage.getHeight(); ++y) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; ++x)
                line.append(String.format("%4d"+(x != width-1 ? "," : ""),nativeImage.getOpacity(x, y)));
            System.out.println(line);
        }
    }
}
