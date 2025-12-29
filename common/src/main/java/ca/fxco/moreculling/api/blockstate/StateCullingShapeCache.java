package ca.fxco.moreculling.api.blockstate;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * StateCullingShapeCache is an interface to access the shape cache.
 *
 * @since 1.2.3
 */
public interface StateCullingShapeCache {

    /**
     * Get the voxelshape of a culling face
     *
     * @param face The face to get the shape from
     * @return The VoxelShape for the face.
     * @since 1.2.3
     */
    VoxelShape moreculling$getFaceCullingShape(Direction face);

    /**
     * Initialize custom culling shape on resource reload
     *
     * @since 1.6.0
     */
    void moreculling$initCustomCullingShape(BlockStateModel model);

}
