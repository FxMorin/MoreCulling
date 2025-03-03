package ca.fxco.moreculling.mixin.blocks.cullshape;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BaseTorchBlock.class)
public abstract class BaseTorchBlock_voxelMixin extends Block implements MoreBlockCulling {
    @Unique
    private static final VoxelShape moreculling$occlusionShape = Block.box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);

    protected BaseTorchBlock_voxelMixin(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return moreculling$occlusionShape;
    }
}
