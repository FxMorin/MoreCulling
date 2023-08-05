package ca.fxco.moreculling.api.map;

/**
 * MapOpacity is an interface that should be used on classes that extend MapState
 * Allows you to cache map translucency
 *
 * @since 0.9.0
 */
public interface MapOpacity {

    /**
     * States if the map texture has any transparent/clear pixels.
     * MoreCulling will be able to skip one face of the item frame if it has no transparency
     *
     * @since 0.9.0
     */
    default boolean hasTransparency() {
        return true;
    }
}
