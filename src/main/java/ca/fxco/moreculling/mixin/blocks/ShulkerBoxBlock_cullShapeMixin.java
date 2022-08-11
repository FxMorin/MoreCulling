package ca.fxco.moreculling.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlock_cullShapeMixin extends Block {

    public ShulkerBoxBlock_cullShapeMixin(Settings settings) {
        super(settings);
    }


    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        VoxelShape voxelShape = state.getOutlineShape(world, pos);
        if (!voxelShape.equals(VoxelShapes.fullCube())) return VoxelShapes.empty();
        return voxelShape;
    }
}
