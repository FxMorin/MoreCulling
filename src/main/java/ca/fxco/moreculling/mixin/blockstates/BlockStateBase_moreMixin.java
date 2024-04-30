package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBase_moreMixin implements MoreStateCulling {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @Override
    public final boolean moreculling$usesCustomShouldDrawFace() {
        return ((MoreBlockCulling) this.getBlock()).moreculling$usesCustomShouldDrawFace(this.asState());
    }

    @Override
    public final Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState sideState,
                                                                    BlockPos thisPos, BlockPos sidePos,
                                                                    Direction side) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$customShouldDrawFace(
                view, this.asState(), sideState, thisPos, sidePos, side
        );
    }

    @Override
    public boolean moreculling$shouldAttemptToCull(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$shouldAttemptToCull(this.asState(), side);
    }

    @Override
    public final boolean moreculling$cantCullAgainst(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$cantCullAgainst(this.asState(), side);
    }

    @Override
    public final boolean moreculling$canCull() {
        return ((MoreBlockCulling) this.getBlock()).moreculling$canCull();
    }
}
