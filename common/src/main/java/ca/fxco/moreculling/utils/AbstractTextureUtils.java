package ca.fxco.moreculling.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class AbstractTextureUtils {

    public static boolean doesHaveTranslucency(ResourceLocation location, AbstractTexture texture) {
        if (texture instanceof DynamicTexture dynamic) {
            return SpriteUtils.doesHaveTranslucency(dynamic.getPixels(), null);
        } else if (texture instanceof SimpleTexture simple) {
            SimpleTexture.TextureImage img = SimpleTexture.TextureImage.load(
                    Minecraft.getInstance().getResourceManager(),
                    simple.location
            );
            boolean translucency = true;
            try {
                translucency = SpriteUtils.doesHaveTranslucency(img.getImage(), null);
                img.close();
            } catch (IOException ignored) {}
            return translucency;
        } else if (texture instanceof TextureAtlas atlas) {
            TextureAtlasSprite sprite = atlas.getSprite(location); // TODO: try-catch
            return SpriteUtils.doesHaveTransparency(sprite); // TODO: Create translucency check
        }
        return true;
    }
}
