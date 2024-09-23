package ca.fxco.moreculling.api.sprite;

import ca.fxco.moreculling.api.data.QuadBounds;
import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * SpriteOpacity is an interface that should be used on classes that extend TextureAtlasSprite
 * It allows your custom sprite to take full advantage of MoreCulling's culling techniques.
 *
 * @since 0.3.0
 */

public interface SpriteOpacity {

    /**
     * Change the images in a sprite that should be used for culling.
     * Only use this if you know what you are doing!
     *
     * @since 0.25.0
     */
    NativeImage moreculling$getUnmipmappedImage();

    /**
     * States if the sprite has any fully transparent pixels.
     * This is currently unused, although it might be used in the future to provide smaller optimizations.
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTransparency() {
        return true;
    }

    /**
     * States if the sprite has any fully transparent pixels. It will only check within the bounds provided.
     * This is currently unused, although it might be used in the future to provide smaller optimizations.
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTransparency(QuadBounds bounds) {
        return true;
    }

    /**
     * States if the sprite has any translucent pixels.
     * MoreCulling will skip optimizations on blocks that use these sprites
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTranslucency() {
        return true;
    }

    /**
     * States if the sprite has any translucent pixels. It will only check within the bounds provided.
     * MoreCulling will skip optimizations on blocks that use these sprites
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTranslucency(QuadBounds bounds) {
        return true;
    }

    /**
     * States if the sprite has any translucent pixels.
     * Although this method gives you the option to pass a list of native image arrays. These native image arrays are
     * the layered images below the face quads. So they should be checked also, since the texture may not actually be
     * transparent.
     * MoreCulling will skip optimizations on blocks that use these sprites and match the conditions
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTranslucency(@Nullable List<NativeImage> quadNatives) {
        return true;
    }

    /**
     * States if the sprite has any translucent pixels. It will only check within the bounds provided.
     * Although this method gives you the option to pass a list of native image arrays. These native image arrays are
     * the layered images below the face quads. So they should be checked also, since the texture may not actually be
     * transparent.
     * MoreCulling will skip optimizations on blocks that use these sprites and match the conditions
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTranslucency(QuadBounds bounds, @Nullable List<NativeImage> quadNatives) {
        return true;
    }
}
