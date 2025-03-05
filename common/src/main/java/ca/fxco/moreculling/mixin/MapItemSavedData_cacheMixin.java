package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.map.MapOpacity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.util.List;

@Mixin(MapItemSavedData.class)
public class MapItemSavedData_cacheMixin implements MapOpacity {

    @Shadow public byte[] colors;
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
            method = "<init>(Lnet/minecraft/resources/ResourceKey;IIBLjava/nio/ByteBuffer;ZZZLjava/util/List;Ljava/util/List;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;colors:[B",
                    opcode = 181, // Opcodes.PUTFIELD
                    shift = At.Shift.AFTER
            )
    )
    private void moreculling$onLoad(ResourceKey p_401030_, int p_401084_, int p_401048_, byte p_401197_,
                                    ByteBuffer p_401348_, boolean p_401353_, boolean p_401003_, boolean p_401306_,
                                    List p_401007_, List p_401318_, CallbackInfo ci) {
        for (byte b : colors) {
            if (b == 0) {
                moreculling$hasTransparency = true;
                return;
            }
        }
    }
}
