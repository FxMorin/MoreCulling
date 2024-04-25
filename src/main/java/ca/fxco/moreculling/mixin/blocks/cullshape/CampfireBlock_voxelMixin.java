package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.minecraft.block.CampfireBlock.FACING;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlock_voxelMixin extends Block {

    @Unique
    private static final VoxelShape moreculling$CULL_SHAPE_X = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 1, 16, 4, 5),
            Block.createCuboidShape(11, 3, 0, 15, 7, 16),
            Block.createCuboidShape(0, 0, 11, 16, 4, 15),
            Block.createCuboidShape(1, 3, 0, 5, 7, 16),
            Block.createCuboidShape(0, 0, 5, 16, 1, 11)
    );

    @Unique
    private static final VoxelShape moreculling$CULL_SHAPE_Z = VoxelShapes.union(
            Block.createCuboidShape(1, 0, 0, 5, 4, 16),
            Block.createCuboidShape(0, 3, 11, 16, 7, 15),
            Block.createCuboidShape(11, 0, 0, 15, 4, 16),
            Block.createCuboidShape(0, 3, 1, 16, 7, 5),
            Block.createCuboidShape(5, 0, 0, 11, 1, 16)
    );

    public CampfireBlock_voxelMixin(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? moreculling$CULL_SHAPE_X : moreculling$CULL_SHAPE_Z;
    }
}
