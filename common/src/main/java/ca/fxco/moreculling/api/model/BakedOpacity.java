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
     * States if any of the textures of the model that are on a face of the block are translucent.
     * If they are not translucent, MoreCulling will be able to provide faster culling for its states.
     * <p>
     * Some baked models will require a blockstate in order to provide more accurate translucency checks,
     * usually if no blockstate is passed it will work fine, although some baked models will always return true.
     * If possible, the default state of the block will be used.
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return true;
    }

    /**
     * This just acts like hasTextureTranslucency(state, null)
     * Using this method is slower than if you also pass the direction
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTextureTranslucency(@Nullable BlockState state) {
        return moreculling$hasTextureTranslucency(state, null);
    }

    /**
     * This just acts like hasTextureTranslucency(null, null)
     * Using this method is slower than the others
     *
     * @since 0.25.0
     */
    default boolean moreculling$hasTextureTranslucency() {
        return moreculling$hasTextureTranslucency(null, null);
    }

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
     * Tells you if this model supports setting the culling shape
     *
     * @since 0.25.0
     */
    @ApiStatus.Internal
    default boolean moreculling$canSetCullingShape() {
        return false;
    }
}
