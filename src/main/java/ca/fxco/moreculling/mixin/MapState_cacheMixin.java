package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.map.MapOpacity;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItemSavedData.class)
public class MapState_cacheMixin implements MapOpacity {

    @Unique
    private boolean moreculling$hasTransparency = false;

    @Override
    public boolean moreculling$hasTransparency() {
        return this.moreculling$hasTransparency;
    }


    @Inject(
            method = "setColor",
            at = @At("HEAD")
    )
    private void moreculling$onSetColor(int x, int z, byte color, CallbackInfo ci) {
        if (color == 0) {
            this.moreculling$hasTransparency = true;
        }
    }
}
