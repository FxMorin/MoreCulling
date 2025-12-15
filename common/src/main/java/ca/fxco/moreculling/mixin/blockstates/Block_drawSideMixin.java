package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;

@Mixin(value = Block.class, priority = 2500)
public class Block_drawSideMixin implements MoreBlockCulling {

    @Unique
    private boolean moreculling$allowCulling;

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return state.is(DONT_CULL);
    }

    @Override
    public boolean moreculling$shouldAttemptToCull(BlockState state, Direction side, BlockGetter level, BlockPos pos) {
        return !((MoreStateCulling) state).moreculling$hasQuadsOnSide(side);
    }

    @Override
    public boolean moreculling$shouldAttemptToCullAgainst(BlockState state, Direction side, BlockGetter level, BlockPos pos) {
        return !((MoreStateCulling) state).moreculling$hasTextureTranslucency(side);
    }

    @Override
    public boolean moreculling$canCull() {
        return this.moreculling$allowCulling;
    }

    @Override
    public void moreculling$setCanCull(boolean canCull) {
        this.moreculling$allowCulling = canCull;
    }
}
