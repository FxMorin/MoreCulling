package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.mixin.accessors.LevelRendererAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRenderer_rainMixin {
    @Inject(
            method = "getPrecipitationAt",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBiome(" +
                            "Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;",
                    shift = At.Shift.BEFORE
            )
    )
    private void moreculling$checkRainFrustum(Level level, BlockPos mutable,
                                              CallbackInfoReturnable<Biome.Precipitation> cir,
                                              @Share("skipLoop") LocalBooleanRef skipLoopRef) {
        if (!MoreCulling.CONFIG.rainCulling) {
            return;
        }
        skipLoopRef.set(!((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getFrustum().isVisible(new AABB(
                mutable.getX() + 1,
                level.getHeight(),
                mutable.getZ() + 1,
                mutable.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, mutable.getX(), mutable.getZ()),
                mutable.getZ()
        )));
    }

    @Inject(
            method = "getPrecipitationAt",
            at = @At(
                    value = "RETURN",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void moreculling$skipRainLoop(Level level, BlockPos blockPos,
                                          CallbackInfoReturnable<Biome.Precipitation> cir,
                                          @Share("skipLoop") LocalBooleanRef skipLoopRef, @Local Biome biome) {
        if (skipLoopRef.get() && !biome.hasPrecipitation()) {
            cir.setReturnValue(Biome.Precipitation.NONE);
        }
    }
}
