package ca.fxco.moreculling.api.sprite;

public interface SpriteOpacity {

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
