package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.minecraft.world.level.block.CampfireBlock.FACING;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlock_voxelMixin extends Block {

    @Unique
    private static final VoxelShape moreculling$CULL_SHAPE_X = Shapes.or(
            Block.box(0, 0, 1, 16, 4, 5),
            Block.box(11, 3, 0, 15, 7, 16),
            Block.box(0, 0, 11, 16, 4, 15),
            Block.box(1, 3, 0, 5, 7, 16),
            Block.box(0, 0, 5, 16, 1, 11)
    );

    @Unique
    private static final VoxelShape moreculling$CULL_SHAPE_Z = Shapes.or(
            Block.box(1, 0, 0, 5, 4, 16),
            Block.box(0, 3, 11, 16, 7, 15),
            Block.box(11, 0, 0, 15, 4, 16),
            Block.box(0, 3, 1, 16, 7, 5),
            Block.box(5, 0, 0, 11, 1, 16)
    );

    public CampfireBlock_voxelMixin(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ?
                moreculling$CULL_SHAPE_X : moreculling$CULL_SHAPE_Z;
    }
}
