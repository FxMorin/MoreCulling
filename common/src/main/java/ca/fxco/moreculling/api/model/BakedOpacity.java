package ca.fxco.moreculling.api.model;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * BakedOpacity is an interface that should be used on classes that extend BakedModel
 * It allows your custom models to take full advantage of MoreCulling's culling techniques.
 *
 * @since 0.3.0
 */

public interface BakedOpacity {

    /**
     * When called this method will reset the translucency cache of the model.
     * This should be called if the texture of the model is ever changed!
     *
     * @since 1.2.3
     */
    default void moreculling$resetTranslucencyCache(BlockState state) {}

    /**
     * Gets the VoxelShape culling shape for the baked model.
     * Returns null unless its set within the model json `cullshapes`
     * WeightedBakedModels cannot use this as we cannot determine which model will be used
     *
     * @since 0.25.0
     */
    default @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        return null;
    }

    /**
     * Used to set the culling shape of the baked model
     *
     * @since 0.25.0
     */
    @ApiStatus.Internal
    default void moreculling$setCullingShape(@Nullable VoxelShape cullingShape) {}

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
     * Tells you if this model supports setting the culling shape
     *
     * @since 0.25.0
     */
    @ApiStatus.Internal
    default boolean moreculling$canSetCullingShape() {
        return false;
    }
}
