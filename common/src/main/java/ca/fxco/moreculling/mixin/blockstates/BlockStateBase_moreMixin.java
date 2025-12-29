package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.BitUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBase_moreMixin implements MoreStateCulling {

    @Unique
    private byte moreculling$emptyFaces = -1;
    @Unique
    private boolean moreculling$hasTextureTranslucency = true;

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @Shadow
    @Final
    private boolean canOcclude;

    @Override
    public boolean moreculling$hasQuadsOnSide(@Nullable Direction direction) {
        if (moreculling$emptyFaces == -1) {
            moreculling$emptyFaces = 0;
            if (!canOcclude && blockRenderManager != null) {
                ((BakedOpacity) blockRenderManager.getBlockModel(asState())).moreculling$resetTranslucencyCache(asState());
            }
        }
        if (direction == null) {
            return moreculling$emptyFaces != BitUtils.ALL_DIRECTIONS;
        }
        return !BitUtils.get(moreculling$emptyFaces, direction.ordinal());
    }

    @Override
    public void moreculling$setHasQuadsOnSide(byte value) {
        moreculling$emptyFaces = value;
    }

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable Direction direction) {
        return moreculling$hasTextureTranslucency;
    }

    @Override
    public void moreculling$setHasTextureTranslucency(boolean value) {
        moreculling$hasTextureTranslucency = value;
    }

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
    public boolean moreculling$shouldAttemptToCull(Direction side, BlockGetter level, BlockPos pos) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$shouldAttemptToCull(this.asState(), side, level, pos);
    }

    @Override
    public boolean moreculling$shouldAttemptToCullAgainst(Direction side, BlockGetter level, BlockPos pos) {
        return ((MoreBlockCulling) this.getBlock()).moreculling$shouldAttemptToCullAgainst(this.asState(), side, level, pos);
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
