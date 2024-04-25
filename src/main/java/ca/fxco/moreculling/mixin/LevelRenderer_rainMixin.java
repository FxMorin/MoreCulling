package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelRenderer.class)
public class LevelRenderer_rainMixin {

    @Shadow
    private Frustum cullingFrustum;

    @Inject(
            method = "renderSnowAndRain",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBiome(" +
                            "Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;",
                    shift = At.Shift.BEFORE
            )
    )
    private void moreculling$checkRainFrustum(LightTexture manager, float tickDelta, double cameraX,
                                              double cameraY, double cameraZ, CallbackInfo ci, float f, Level level,
                                              int i, int j, int k, Tesselator tessellator,
                                              BufferBuilder bufferBuilder, int l, int m, float g,
                                              BlockPos.MutableBlockPos mutable,
                                              @Share("skipLoop") LocalBooleanRef skipLoopRef) {
        if (!MoreCulling.CONFIG.rainCulling) {
            return;
        }
        skipLoopRef.set(!this.cullingFrustum.isVisible(new AABB(
                mutable.getX() + 1,
                level.getHeight(),
                mutable.getZ() + 1,
                mutable.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, mutable.getX(), mutable.getZ()),
                mutable.getZ()
        )));
    }

    @WrapOperation(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;hasPrecipitation()Z"
            )
    )
    private boolean moreculling$skipRainLoop(Biome instance, Operation<Boolean> original,
                                             @Share("skipLoop") LocalBooleanRef skipLoopRef) {
        return !skipLoopRef.get() && original.call(instance);
    }
}
