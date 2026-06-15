package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.renderers.ExtendedLevelExtractor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRenderer_rainMixin {

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;" +
                            "getPrecipitationAt(Lnet/minecraft/core/BlockPos;" +
                            ")Lnet/minecraft/world/level/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation moreculling$checkRainFrustum(ClientLevel instance, BlockPos pos,
                                                             Operation<Biome.Precipitation> original) {
        if (!MoreCulling.CONFIG.rainCulling) {
            return original.call(instance, pos);
        }

        if (!((ExtendedLevelExtractor) Minecraft.getInstance().levelExtractor).moreculling$getFrustum().isVisible(new AABB(
                pos.getX() + 1,
                instance.getHeight(),
                pos.getZ() + 1,
                pos.getX(),
                instance.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()),
                pos.getZ()
        ))) {
            return Biome.Precipitation.NONE;
        }

        return original.call(instance, pos);
    }

}
