package ca.fxco.moreculling.api.sprite;

import net.minecraft.client.texture.NativeImage;

public interface SpriteOpacity {

    /**
     * Change the images in a sprite that should be used for culling.
     * Only use this if you know what you are doing!
     */
    NativeImage[] getImages();

    /**
     * States if the sprite has any fully transparent pixels.
     * This is currently unused, although it might be used in the future to provide smaller optimizations.
     */
    default boolean hasTransparency() {
        return true;
    }

    /**
     * States if the sprite has any translucent pixels.
     * MoreCulling will skip optimizations on blocks that use these sprites
     */
    default boolean hasTranslucency() {
        return true;
    }
}
