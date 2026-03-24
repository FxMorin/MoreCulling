package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.renderers.ExtendedLevelRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRenderer_frustumMixin implements ExtendedLevelRenderer {
    @Unique
    private Frustum moreculling$cullingFrustum;

    @WrapOperation(
            method = "extractLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;getCullFrustum()Lnet/minecraft/client/renderer/culling/Frustum;"
            )
    )
    private Frustum storeFrustum(Camera instance, Operation<Frustum> original) {
        Frustum frustum = original.call(instance);

        moreculling$cullingFrustum = frustum;

        return frustum;
    }

    @Override
    public Frustum moreculling$getFrustum() {
        return moreculling$cullingFrustum;
    }
}
