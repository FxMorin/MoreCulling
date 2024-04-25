package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChorusFlowerBlock.class)
public class ChorusFlowerBlock_voxelMixin extends Block {

    @Unique
    private static final VoxelShape moreculling$CULL_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 2.0, 2.0, 16.0, 14.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0),
            Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 16.0)
    );

    public ChorusFlowerBlock_voxelMixin(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return moreculling$CULL_SHAPE;
    }
}
