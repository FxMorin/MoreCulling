package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.mixin.accessors.LevelRendererAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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

    @WrapOperation(
            method = "collectColumnInstances",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;" +
                            "getPrecipitationAt(Lnet/minecraft/world/level/Level;" +
                            "Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation moreculling$checkRainFrustum(WeatherEffectRenderer instance, Level level, BlockPos pos,
                                                             Operation<Biome.Precipitation> original) {
        if (!MoreCulling.CONFIG.rainCulling) {
            return original.call(instance, level, pos);
        }

        if (!((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getFrustum().isVisible(new AABB(
                pos.getX() + 1,
                level.getHeight(),
                pos.getZ() + 1,
                pos.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()),
                pos.getZ()
        ))) {
            return Biome.Precipitation.NONE;
        }

        return original.call(instance, level, pos);
    }

}
