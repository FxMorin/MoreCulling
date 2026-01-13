package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TorchBlock.class)
public class TorchBlock_voxelMixin extends Block {
    @Unique
    private static final VoxelShape moreculling$occlusionShape = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);

    public TorchBlock_voxelMixin(Settings settings) {
        super(settings);
    }


    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return moreculling$occlusionShape;
    }
}
