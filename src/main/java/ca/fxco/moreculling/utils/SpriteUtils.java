package ca.fxco.moreculling.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.lwjgl.system.MemoryUtil;

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
            for (int y = 0; y < nativeImage.getHeight(); ++y)
                for (int x = 0; x < width; ++x)
                    if (SpriteUtils.getOpacity(nativeImage, x, y) == 0)
                        return true;
        }
        return false;
    }

    public static boolean doesHaveTranslucency(NativeImage nativeImage) {
        if (nativeImage.getFormat().hasOpacityChannel()) {
            int width = nativeImage.getWidth();
            for (int y = 0; y < nativeImage.getHeight(); ++y)
                for (int x = 0; x < width; ++x)
                    if (SpriteUtils.getOpacity(nativeImage, x, y) != -1)
                        return true;
        }
        return false;
    }

    // Minecraft's Luminance-Alpha format was screwing some results. We look at alpha directly here instead
    public static byte getOpacity(NativeImage img, int x, int y) {
        if (!img.getFormat().hasAlpha()) return 0;
        int i = (x + y * img.getWidth()) * img.getFormat().getChannelCount() + img.getFormat().getAlphaOffset() / 8;
        return MemoryUtil.memGetByte(img.pointer + (long)i);
    }

    public static void printOpacity(NativeImage nativeImage) {
        if (nativeImage.getFormat().hasOpacityChannel()) {
            int width = nativeImage.getWidth();
            for (int y = 0; y < nativeImage.getHeight(); ++y) {
                StringBuilder line = new StringBuilder();
                for (int x = 0; x < width; ++x)
                    line.append(String.format("%4d"+(x != width-1 ? "," : ""),SpriteUtils.getOpacity(nativeImage, x, y)));
                System.out.println(line);
            }
        }
    }
}
