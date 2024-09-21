package ca.fxco.moreculling.api.model;

import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.resources.ResourceLocation;


import java.util.List;

/**
 * ExtendedUnbakedModel is an interface that should be used on classes that extend JsonUnbakedModel
 * It allows unbaked models to use custom cullshapes and modify some inner workings
 *
 * @since 0.15.0
 */
public interface ExtendedUnbakedModel {

    /**
     * Sets the CullShapeElement for the extended unbaked model
     *
     * @since 0.15.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$setCullShapeElements}
     */
    @Deprecated(forRemoval = true)
    default void setCullShapeElements(List<CullShapeElement> cullShapeElements) {
        moreculling$setCullShapeElements(cullShapeElements);
    }

    /**
     * Sets the CullShapeElement for the extended unbaked model
     *
     * @since 0.25.0
     */
    void moreculling$setCullShapeElements(List<CullShapeElement> cullShapeElements);

    /**
     * Gets the CullShapeElement for the extended unbaked model
     *
     * @since 0.15.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$getCullShapeElements}
     */
    @Deprecated(forRemoval = true)
    default List<CullShapeElement> getCullShapeElements(ResourceLocation id) {
        return moreculling$getCullShapeElements(id);
    }

    /**
     * Gets the CullShapeElement for the extended unbaked model
     *
     * @since 0.25.0
     */
    List<CullShapeElement> moreculling$getCullShapeElements(ResourceLocation id);

    /**
     * When set to true, the cull shape is completely ignored and instead the model will be used as the cull shape
     *
     * @since 0.15.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$setUseModelShape}
     */
    @Deprecated(forRemoval = true)
    default void setUseModelShape(boolean useModelShape) {
        moreculling$setUseModelShape(useModelShape);
    }

    /**
     * When set to true, the cull shape is completely ignored and instead the model will be used as the cull shape
     *
     * @since 0.25.0
     */
    void moreculling$setUseModelShape(boolean useModelShape);

    /**
     * Returns if it should use the model shape instead of the cull shape
     *
     * @since 0.15.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$getUseModelShape}
     */
    @Deprecated(forRemoval = true)
    default boolean getUseModelShape(ResourceLocation id) {
        return moreculling$getUseModelShape(id);
    }

    /**
     * Returns if it should use the model shape instead of the cull shape
     *
     * @since 0.25.0
     */
    boolean moreculling$getUseModelShape(ResourceLocation id);

    /**
     * ModelElementFace is just final's, that should not change. However, you can create a new ModelElementFace or wrap
     * it. Do this to modify the element face data.
     *
     * @since 0.15.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$modifyElementFace}
     */
    @Deprecated(forRemoval = true)
    default BlockElementFace modifyElementFace(BlockElementFace elementFace) {
        return moreculling$modifyElementFace(elementFace);
    }

    /**
     * ModelElementFace is just final's, that should not change. However, you can create a new ModelElementFace or wrap
     * it. Do this to modify the element face data.
     *
     * @since 0.25.0
     */
    BlockElementFace moreculling$modifyElementFace(BlockElementFace elementFace);
}
