package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedPaintingRenderState;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PaintingRenderState.class)
public class PaintingRenderState_apiMixin implements ExtendedPaintingRenderState {

    @Unique
    private BlockPos[][] moreculling$paintingPositions;

    @Override
    public BlockPos[][] moreculling$getPaintingPositions() {
        return this.moreculling$paintingPositions;
    }

    @Override
    public void moreculling$setPaintingPositions(BlockPos[][] positions) {
        this.moreculling$paintingPositions = positions;
    }
}
