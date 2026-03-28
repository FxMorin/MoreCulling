package ca.fxco.moreculling.api.renderers;

/**
 * ExtendedMapRenderState is an interface that should be used on classes that extend MapRenderState
 * Allows you to cache map translucency
 *
 * @since 1.7.0
 */
public interface ExtendedMapRenderState {

    /**
     * States if the map texture has any transparent/clear pixels.
     * MoreCulling will be able to skip one face of the item frame if it has no transparency
     *
     * @since 1.7.0
     */
    default boolean moreculling$hasTransparency() {
        return true;
    }

    /**
     * Sets if the map texture has any transparent/clear pixels.
     * MoreCulling will be able to skip one face of the item frame if it has no transparency
     *
     * @since 1.7.0
     */
    default void moreculling$setHasTransparency(boolean transparency) {
    }
}
