package ca.fxco.moreculling.api.model;

import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;


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
     * @since 0.25.0
     */
    default void moreculling$setCullShapeElements(List<CullShapeElement> cullShapeElements) {}

    /**
     * Gets the CullShapeElement for the extended unbaked model
     *
     * @since 0.25.0
     */
    default List<CullShapeElement> moreculling$getCullShapeElements(ResolvedModel parent) {
        return List.of();
    }

    /**
     * When set to true, the cull shape is completely ignored and instead the model will be used as the cull shape
     *
     * @since 0.25.0
     */
    default void moreculling$setUseModelShape(boolean useModelShape) {}

    /**
     * Returns if it should use the model shape instead of the cull shape
     *
     * @since 0.25.0
     */
    default boolean moreculling$getUseModelShape(Identifier id) {
        return false;
    }

    /**
     * When set to false, the cull shape is completely ignored and instead the model
     * will be used as the cull shape even if block state can occlude
     *
     * @since 1.2.10
     */
    default void moreculling$setHasAutoModelShape(boolean hasAutoModelShape) {}

    /**
     * Returns if useModelShape was set automatically
     *
     * @since 1.2.10
     */
    default boolean moreculling$getHasAutoModelShape() {
        return false;
    }

    /**
     * ModelElementFace is just final's, that should not change. However, you can create a new ModelElementFace or wrap
     * it. Do this to modify the element face data.
     *
     * @since 0.25.0
     */
    BlockElementFace moreculling$modifyElementFace(BlockElementFace elementFace);
}
