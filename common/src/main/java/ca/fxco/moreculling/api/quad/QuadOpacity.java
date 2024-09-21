package ca.fxco.moreculling.api.quad;

/**
 * BakedOpacity is an interface that should be used on classes that extend BakedQuad
 * It allows your custom quads to take full advantage of MoreCulling's culling techniques.
 * This is only used for quads that get used in BakedModels that contain more than 1 model
 *
 * @since 0.12.0
 */
public interface QuadOpacity {

    /**
     * States if the texture of the quad has any translucency.
     * This method should set the translucency state if it has not been initialized yet
     * If they are not translucent, MoreCulling will be able to provide faster culling for some models it's applied on.
     *
     * @since 0.12.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$getTextureTranslucency}
     */
    @Deprecated(forRemoval = true)
    default boolean getTextureTranslucency() {
        return moreculling$getTextureTranslucency();
    }

    /**
     * States if the texture of the quad has any translucency.
     * This method should set the translucency state if it has not been initialized yet
     * If they are not translucent, MoreCulling will be able to provide faster culling for some models it's applied on.
     *
     * @since 0.25.0
     */
    default boolean moreculling$getTextureTranslucency() {
        return true;
    }

    /**
     * When called this method will reset the translucency cache of the model.
     * This should be called if the texture of the quad is ever changed!
     *
     * @since 0.12.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$resetTranslucencyCache}
     */
    @Deprecated(forRemoval = true)
    default void resetTranslucencyCache() {
        moreculling$resetTranslucencyCache();
    }

    /**
     * When called this method will reset the translucency cache of the model.
     * This should be called if the texture of the quad is ever changed!
     *
     * @since 0.12.0
     */
    default void moreculling$resetTranslucencyCache() {}
}
