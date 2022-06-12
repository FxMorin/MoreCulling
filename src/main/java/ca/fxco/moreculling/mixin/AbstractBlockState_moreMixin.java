package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.patches.MoreStateCulling;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_moreMixin implements MoreStateCulling {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Override
    public boolean isSideInvisibleAtPos(BlockState state, Direction direction, BlockPos pos) {
        return ((MoreBlockCulling)this.getBlock()).isSideInvisibleAtPos(this.asBlockState(), state, direction, pos);
    }

    @Override
    public boolean usesCustomShouldDrawFace() {
        return ((MoreBlockCulling)this.getBlock()).usesCustomShouldDrawFace(this.asBlockState());
    }

    @Override
    public boolean customShouldDrawFace(BlockView view, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side) {
        return ((MoreBlockCulling)this.getBlock()).customShouldDrawFace(view, this.asBlockState(), sideState, thisPos, sidePos, side);
    }
}
