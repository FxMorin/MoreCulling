package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public class WorldRenderer_rainMixin {

    @Shadow private Frustum frustum;
    private boolean shouldSkipLoop = false;

    @Inject(
            method = "renderWeather",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBiome(" +
                            "Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/registry/entry/RegistryEntry;",
                    shift = At.Shift.BEFORE
            )
    )
    private void checkRainFrustum(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY,
                                  double cameraZ, CallbackInfo ci, float f, World world, int i, int j, int k,
                                  Tessellator tessellator, BufferBuilder bufferBuilder, int l, int m, float g,
                                  BlockPos.Mutable mutable) {
        shouldSkipLoop = MoreCulling.CONFIG.rainCulling && !this.frustum.isVisible(new Box(
                mutable.getX() + 1,
                world.getHeight(),
                mutable.getZ() + 1,
                mutable.getX(),
                world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable.getX(), mutable.getZ()),
                mutable.getZ()
        ));
    }

    @Redirect(
            method = "renderWeather",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/Biome;" +
                            "getPrecipitation()Lnet/minecraft/world/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation skipRainLoop(Biome instance) {
        return shouldSkipLoop ? Biome.Precipitation.NONE : instance.getPrecipitation();
    }
}
