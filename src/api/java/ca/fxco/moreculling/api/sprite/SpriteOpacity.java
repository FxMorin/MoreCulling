package ca.fxco.moreculling.api.sprite;

import net.minecraft.client.texture.NativeImage;

/**
 * SpriteOpacity is an interface that should be used on classes that extend Sprite
 * It allows your custom sprite to take full advantage of MoreCulling's culling techniques.
 * @since 0.3.0
 */

public interface SpriteOpacity {

    /**
     * Change the images in a sprite that should be used for culling.
     * Only use this if you know what you are doing!
     * @since 0.7.0
     */
    NativeImage[] getImages();

    /**
     * States if the sprite has any fully transparent pixels.
     * This is currently unused, although it might be used in the future to provide smaller optimizations.
     * @since 0.3.0
     */
    default boolean hasTransparency() {
        return true;
    }

    /**
     * States if the sprite has any translucent pixels.
     * MoreCulling will skip optimizations on blocks that use these sprites
     * @since 0.3.0
     */
    default boolean hasTranslucency() {
        return true;
    }
}
