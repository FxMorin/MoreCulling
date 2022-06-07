package ca.fxco.moreculling.api.model;

public interface BakedTransparency {

    /**
     * States if any of the textures of the model that are on a face of the block are transparent.
     * If they are not transparent, MoreCulling will be able to provide faster culling for its states.
     */
    default boolean hasTextureTransparency() {
        return true;
    }
}
