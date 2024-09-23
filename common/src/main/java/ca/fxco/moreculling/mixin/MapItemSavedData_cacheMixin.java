package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.map.MapOpacity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItemSavedData.class)
public class MapItemSavedData_cacheMixin implements MapOpacity {

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

    @Inject(
            method = "load",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;colors:[B",
                    opcode = 181, // Opcodes.PUTFIELD
                    shift = At.Shift.AFTER
            )
    )
    private static void moreculling$onLoad(CompoundTag tag, HolderLookup.Provider provider,
                                           CallbackInfoReturnable<MapItemSavedData> cir, @Local MapItemSavedData map) {
        for (byte b : map.colors) {
            if (b == 0) {
                ((MapItemSavedData_cacheMixin)(Object)map).moreculling$hasTransparency = true;
                return;
            }
        }
    }
}
