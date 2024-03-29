package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.map.MapOpacity;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapState.class)
public class MapState_cacheMixin implements MapOpacity {

    @Unique
    private boolean hasTransparency = false;

    @Override
    public boolean hasTransparency() {
        return this.hasTransparency;
    }


    @Inject(
            method = "setColor",
            at = @At("HEAD")
    )
    private void onSetColor(int x, int z, byte color, CallbackInfo ci) {
        if (color == 0) {
            this.hasTransparency = true;
        }
    }
}
