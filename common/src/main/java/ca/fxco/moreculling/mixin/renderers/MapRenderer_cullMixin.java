package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.map.MapOpacity;
import ca.fxco.moreculling.api.renderers.ExtendedMapRenderState;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRenderer_cullMixin {
    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "TAIL"
            )
    )
    public void moreculling$checkForTransparency(MapId mapId, MapItemSavedData mapData,
                                                 MapRenderState mapRenderState, CallbackInfo ci) {
        ((ExtendedMapRenderState) mapRenderState)
                .moreculling$setHasTransparency(((MapOpacity) mapData).moreculling$hasTransparency());
    }
}
