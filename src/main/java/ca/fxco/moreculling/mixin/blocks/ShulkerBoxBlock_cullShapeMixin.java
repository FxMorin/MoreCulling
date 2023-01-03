package ca.fxco.moreculling.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlock_cullShapeMixin extends Block {

    public ShulkerBoxBlock_cullShapeMixin(Settings settings) {
        super(settings);
    }


    // Should only be done in LOD Chunks
    /*@Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        VoxelShape voxelShape = state.getOutlineShape(world, pos);
        if (!voxelShape.equals(VoxelShapes.fullCube())) return VoxelShapes.empty();
        return voxelShape;
    }*/
}
