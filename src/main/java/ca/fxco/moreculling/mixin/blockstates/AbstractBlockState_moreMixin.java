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
    public final boolean moreculling$usesCustomShouldDrawFace() {
        return ((MoreBlockCulling) this.getBlock()).moreculling$usesCustomShouldDrawFace(this.asBlockState());
    }

    @Override
    public final Optional<Boolean> moreculling$customShouldDrawFace(BlockView view, BlockState sideState,
                                                                    BlockPos thisPos, BlockPos sidePos,
                                                                    Direction side) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$customShouldDrawFace(
                view, this.asBlockState(), sideState, thisPos, sidePos, side
        );
    }

    @Override
    public boolean moreculling$shouldAttemptToCull(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$shouldAttemptToCull(this.asBlockState(), side);
    }

    @Override
    public final boolean moreculling$cantCullAgainst(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$cantCullAgainst(this.asBlockState(), side);
    }

    @Override
    public final boolean moreculling$canCull() {
        return ((MoreBlockCulling) this.getBlock()).moreculling$canCull();
    }
}
