package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CacheUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_moreMixin implements MoreStateCulling {

    @Unique
    private byte moreculling$emptyFaces = -1;
    @Unique
    private boolean moreculling$hasTextureTranslucency = true;

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Override
    public boolean moreculling$hasQuadsOnSide(@Nullable Direction direction) {
        if (moreculling$emptyFaces == -1) {
            moreculling$emptyFaces = 0;
            BakedModel model = MoreCulling.blockRenderManager.getModel(this.asBlockState());
            if (model != null) {
                ((BakedOpacity) model).resetTranslucencyCache(this.asBlockState());
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
    public final boolean usesCustomShouldDrawFace() {
        return ((MoreBlockCulling) this.getBlock()).usesCustomShouldDrawFace(this.asBlockState());
    }

    @Override
    public final Optional<Boolean> customShouldDrawFace(BlockView view, BlockState sideState,
                                                        BlockPos thisPos, BlockPos sidePos, Direction side) {
        return ((MoreBlockCulling) this.getBlock()).customShouldDrawFace(
                view, this.asBlockState(), sideState, thisPos, sidePos, side
        );
    }

    @Override
    public boolean shouldAttemptToCull() {
        return ((MoreBlockCulling) this.getBlock()).shouldAttemptToCull(this.asBlockState());
    }

    @Override
    public boolean shouldAttemptToCull(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).shouldAttemptToCull(this.asBlockState(), side);
    }

    @Override
    public boolean shouldAttemptToCullAgainst(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).shouldAttemptToCullAgainst(this.asBlockState(), side);
    }

    @Override
    public final boolean cantCullAgainst() {
        return ((MoreBlockCulling) this.getBlock()).cantCullAgainst(this.asBlockState());
    }

    @Override
    public final boolean cantCullAgainst(Direction side) {
        return ((MoreBlockCulling) this.getBlock()).cantCullAgainst(this.asBlockState(), side);
    }

    @Override
    public final boolean canCull() {
        return ((MoreBlockCulling) this.getBlock()).canCull();
    }
}
