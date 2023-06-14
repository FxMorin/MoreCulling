package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_moreMixin implements MoreStateCulling {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Override
    public final boolean usesCustomShouldDrawFace() {
        return ((MoreBlockCulling)this.getBlock()).usesCustomShouldDrawFace(this.asBlockState());
    }

    @Override
    public final Optional<Boolean> customShouldDrawFace(BlockView view, BlockState sideState,
                                                        BlockPos thisPos, BlockPos sidePos, Direction side) {
        return ((MoreBlockCulling)this.getBlock()).customShouldDrawFace(
                view, this.asBlockState(), sideState, thisPos, sidePos, side
        );
    }

    @Override
    public boolean shouldAttemptToCull() {
        return ((MoreBlockCulling)this.getBlock()).shouldAttemptToCull(this.asBlockState());
    }

    @Override
    public boolean shouldAttemptToCull(Direction side) {
        return ((MoreBlockCulling)this.getBlock()).shouldAttemptToCull(this.asBlockState(), side);
    }

    @Override
    public final boolean cantCullAgainst() {
        return ((MoreBlockCulling)this.getBlock()).cantCullAgainst(this.asBlockState());
    }

    @Override
    public final boolean cantCullAgainst(Direction side) {
        return ((MoreBlockCulling)this.getBlock()).cantCullAgainst(this.asBlockState(), side);
    }

    @Override
    public final boolean canCull() {
        return ((MoreBlockCulling)this.getBlock()).canCull();
    }
}
