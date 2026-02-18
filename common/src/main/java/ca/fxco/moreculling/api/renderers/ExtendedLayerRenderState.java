package ca.fxco.moreculling.api.renderers;

/**
 * Interface for internal use to check if item should use lod culling
 *
 * @since 1.6.3
 */
public interface ExtendedLayerRenderState {

    /**
     * If its a block item
     *
     * @since 1.6.3
     */
    boolean moreculling$isBlockItem();

    /**
     * Sets if its block item
     *
     * @since 1.6.3
     */
    void moreculling$setIsBlockItem(boolean isBlockItem);
}
