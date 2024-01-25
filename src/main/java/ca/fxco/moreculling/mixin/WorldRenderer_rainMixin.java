package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
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

    @Shadow
    private Frustum frustum;

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
                                  BlockPos.Mutable mutable, @Share("skipLoop") LocalBooleanRef skipLoopRef) {
        if (!MoreCulling.CONFIG.rainCulling) {
            return;
        }
        skipLoopRef.set(!this.frustum.isVisible(new Box(
                mutable.getX() + 1,
                world.getHeight(),
                mutable.getZ() + 1,
                mutable.getX(),
                world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable.getX(), mutable.getZ()),
                mutable.getZ()
        )));
    }

    @Redirect(
            method = "renderWeather",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/Biome;hasPrecipitation()Z"
            )
    )
    private boolean skipRainLoop(Biome instance, @Share("skipLoop") LocalBooleanRef skipLoopRef) {
        return !skipLoopRef.get() && instance.hasPrecipitation();
    }
}
