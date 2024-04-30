package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpriteUtils {

    public static boolean doesHaveTransparency(TextureAtlasSprite sprite) {
        int width = sprite.contents().width();
        int height = sprite.contents().height();
        for (int frame : sprite.contents().getUniqueFrames().toArray()) {
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    if (sprite.contents().isTransparent(frame, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTransparency(TextureAtlasSprite sprite, QuadBounds bounds) {
        int minWidth = Math.max(0, bounds.getMinX());
        int minHeight = Math.max(0, bounds.getMinY());
        int maxWidth = Math.min(bounds.getMaxX(), sprite.contents().width());
        int maxHeight = Math.min(bounds.getMaxY(), sprite.contents().height());
        for (int frame : sprite.contents().getUniqueFrames().toArray()) {
            for (int y = minHeight; y < maxHeight; ++y) {
                for (int x = minWidth; x < maxWidth; ++x) {
                    if (sprite.contents().isTransparent(frame, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTransparency(NativeImage nativeImage) {
        if (nativeImage.format().hasAlpha()) {
            int width = nativeImage.getWidth();
            for (int y = 0; y < nativeImage.getHeight(); ++y) {
                for (int x = 0; x < width; ++x) {
                    if (nativeImage.getLuminanceOrAlpha(x, y) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTransparency(NativeImage nativeImage, QuadBounds bounds) {
        if (nativeImage.format().hasAlpha()) {
            int minWidth = Math.max(0, bounds.getMinX());
            int maxWidth = Math.min(bounds.getMaxX(), nativeImage.getWidth());
            for (int y = Math.max(0, bounds.getMinY()); y < Math.min(bounds.getMaxY(), nativeImage.getHeight()); ++y) {
                for (int x = minWidth; x < maxWidth; ++x) {
                    if (nativeImage.getLuminanceOrAlpha(x, y) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTranslucency(NativeImage image, @Nullable List<NativeImage> orMatch) {
        return doesHaveTranslucency(image, orMatch, 0, image.getWidth(), 0, image.getHeight());
    }

    public static boolean doesHaveTranslucency(NativeImage image, QuadBounds bounds,
                                               @Nullable List<NativeImage> orMatch) {
        return doesHaveTranslucency(image, orMatch,
                Math.max(0, bounds.getMinX()),
                Math.min(bounds.getMaxX(), image.getWidth()),
                Math.max(0, bounds.getMinY()),
                Math.min(bounds.getMaxY(), image.getHeight())
        );
    }

    private static boolean doesHaveTranslucency(NativeImage image, @Nullable List<NativeImage> orMatch,
                                                int minWidth, int maxWidth, int minHeight, int maxHeight) {
        if (image.format().hasAlpha()) {
            for (int y = minHeight; y < maxHeight; ++y) {
                for (int x = minWidth; x < maxWidth; ++x) {
                    if (image.getLuminanceOrAlpha(x, y) != -1) { //translucent
                        if (orMatch != null) {
                            boolean success = false;
                            for (NativeImage nativeImage : orMatch) {
                                if (x <= nativeImage.getWidth() && y <= nativeImage.getHeight() &&
                                        nativeImage.getLuminanceOrAlpha(x, y) == -1) { // not translucent
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

    public static void printOpacity(TextureAtlasSprite sprite) {
        printOpacity(sprite, null);
    }

    public static void printOpacity(TextureAtlasSprite sprite, @Nullable QuadBounds bounds) {
        NativeImage img = ((SpriteOpacity) sprite).moreculling$getUnmipmappedImage();
        if (img.format().hasLuminanceOrAlpha()) {
            printOpacity(img, bounds);
        }
    }

    public static void printOpacity(NativeImage nativeImage, @Nullable QuadBounds bounds) {
        if (!nativeImage.format().hasLuminanceOrAlpha()) {
            return;
        }
        int minWidth = bounds == null ? 0 : Math.max(0, bounds.getMinX());
        int maxWidth = bounds == null ? nativeImage.getWidth() : Math.min(bounds.getMaxX(), nativeImage.getWidth());
        int minHeight = bounds == null ? 0 : Math.max(0, bounds.getMinY());
        int maxHeight = bounds == null ? nativeImage.getHeight() : Math.min(bounds.getMaxY(), nativeImage.getHeight());
        for (int y = minHeight; y < maxHeight; ++y) {
            StringBuilder line = new StringBuilder();
            for (int x = minWidth; x < maxWidth; ++x) {
                line.append(String.format("%4d" + (x != maxWidth - 1 ? "," : ""), nativeImage.getLuminanceOrAlpha(x, y)));
            }
            System.out.println(line);
        }
    }
}
