package ca.fxco.moreculling.api.model;

public interface BakedOpacity {

    /**
     * States if any of the textures of the model that are on a face of the block are translucent.
     * If they are not translucent, MoreCulling will be able to provide faster culling for its states.
     */
    default boolean hasTextureTranslucency() {
        return true;
    }

    /**
     * When called this method will reset the translucency cache of the model.
     * This should be called if the texture of the model is ever changed!
     */
    default void resetTranslucencyCache() {}
}
