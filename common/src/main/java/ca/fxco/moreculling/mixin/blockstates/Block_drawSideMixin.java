package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.platform.Services;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(value = Block.class, priority = 2500)
public class Block_drawSideMixin implements MoreBlockCulling {

    @Unique
    private boolean allowCulling;

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return state.is(DONT_CULL);
    }

    @Override
    public boolean moreculling$shouldAttemptToCull(BlockState state, Direction side) {
        BakedModel model = blockRenderManager.getBlockModel(state);
        Services.PLATFORM.getQuads(model, state, side, CullingUtils.random);
        return !((BakedOpacity) model).moreculling$hasTextureTranslucency(state, side);
    }

    @Override
    public boolean moreculling$canCull() {
        return this.allowCulling;
    }

    @Override
    public void moreculling$setCanCull(boolean canCull) {
        this.allowCulling = canCull;
    }
}
