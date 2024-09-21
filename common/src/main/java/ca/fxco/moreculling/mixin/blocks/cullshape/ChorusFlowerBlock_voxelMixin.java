package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChorusFlowerBlock.class)
public class ChorusFlowerBlock_voxelMixin extends Block {

    @Unique
    private static final VoxelShape moreculling$CULL_SHAPE = Shapes.or(
            Block.box(0.0, 2.0, 2.0, 16.0, 14.0, 14.0),
            Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0),
            Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 16.0)
    );

    public ChorusFlowerBlock_voxelMixin(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return moreculling$CULL_SHAPE;
    }
}
