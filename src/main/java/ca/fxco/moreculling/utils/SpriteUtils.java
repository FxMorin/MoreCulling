package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpriteUtils {

    public static boolean doesHaveTransparency(Sprite sprite) {
        int width = sprite.getContents().getWidth();
        int height = sprite.getContents().getHeight();
        for (int frame : sprite.getContents().getDistinctFrameCount().toArray()) {
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    if (sprite.getContents().isPixelTransparent(frame, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTransparency(Sprite sprite, QuadBounds bounds) {
        int minWidth = Math.max(0, bounds.getMinX());
        int minHeight = Math.max(0, bounds.getMinY());
        int maxWidth = Math.min(bounds.getMaxX(), sprite.getContents().getWidth());
        int maxHeight = Math.min(bounds.getMaxY(), sprite.getContents().getHeight());
        for (int frame : sprite.getContents().getDistinctFrameCount().toArray()) {
            for (int y = minHeight; y < maxHeight; ++y) {
                for (int x = minWidth; x < maxWidth; ++x) {
                    if (sprite.getContents().isPixelTransparent(frame, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTransparency(NativeImage nativeImage) {
        if (nativeImage.getFormat().hasAlpha()) {
            int width = nativeImage.getWidth();
            for (int y = 0; y < nativeImage.getHeight(); ++y) {
                for (int x = 0; x < width; ++x) {
                    if (nativeImage.getOpacity(x, y) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesHaveTransparency(NativeImage nativeImage, QuadBounds bounds) {
        if (nativeImage.getFormat().hasAlpha()) {
            int minWidth = Math.max(0, bounds.getMinX());
            int maxWidth = Math.min(bounds.getMaxX(), nativeImage.getWidth());
            for (int y = Math.max(0, bounds.getMinY()); y < Math.min(bounds.getMaxY(), nativeImage.getHeight()); ++y) {
                for (int x = minWidth; x < maxWidth; ++x) {
                    if (nativeImage.getOpacity(x, y) == 0) {
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
        if (image.getFormat().hasAlpha()) {
            for (int y = minHeight; y < maxHeight; ++y) {
                for (int x = minWidth; x < maxWidth; ++x) {
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
        printOpacity(sprite, null);
    }

    public static void printOpacity(Sprite sprite, @Nullable QuadBounds bounds) {
        NativeImage img = ((SpriteOpacity) sprite).getUnmipmappedImage();
        if (img.getFormat().hasOpacityChannel()) {
            printOpacity(img, bounds);
        }
    }

    public static void printOpacity(NativeImage nativeImage, @Nullable QuadBounds bounds) {
        if (!nativeImage.getFormat().hasOpacityChannel()) {
            return;
        }
        int minWidth = bounds == null ? 0 : Math.max(0, bounds.getMinX());
        int maxWidth = bounds == null ? nativeImage.getWidth() : Math.min(bounds.getMaxX(), nativeImage.getWidth());
        int minHeight = bounds == null ? 0 : Math.max(0, bounds.getMinY());
        int maxHeight = bounds == null ? nativeImage.getHeight() : Math.min(bounds.getMaxY(), nativeImage.getHeight());
        for (int y = minHeight; y < maxHeight; ++y) {
            StringBuilder line = new StringBuilder();
            for (int x = minWidth; x < maxWidth; ++x) {
                line.append(String.format("%4d" + (x != maxWidth - 1 ? "," : ""), nativeImage.getOpacity(x, y)));
            }
            System.out.println(line);
        }
    }
}
