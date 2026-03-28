package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedMapRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MapRenderState.class)
public class MapRenderState_apiMixin implements ExtendedMapRenderState {
    @Unique
    private boolean moreculling$hasTransparency = true;

    @Override
    public boolean moreculling$hasTransparency() {
        return moreculling$hasTransparency;
    }

    @Override
    public void moreculling$setHasTransparency(boolean transparency) {
        moreculling$hasTransparency = transparency;
    }
}
