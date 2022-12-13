package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static ca.fxco.moreculling.utils.MathUtils.ONE_SIGN_ROTATION;

@Mixin(HangingSignBlockEntityRenderer.class)
public class HangingSignBlockEntityRenderer_textMixin {

    @Inject(
            method = "render(Lnet/minecraft/block/entity/SignBlockEntity;" +
                    "FLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/HangingSignBlockEntityRenderer;" +
                            "renderText(Lnet/minecraft/block/entity/SignBlockEntity;" +
                            "Lnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumerProvider;IF)V"
            ),
            cancellable = true
    )
    private void cullSignText(SignBlockEntity signBlockEntity, float tickDelta, MatrixStack matrixStack,
                              VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci,
                              BlockState blockState) {
        if (MoreCulling.CONFIG.signTextCulling) {
            Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            double angle = blockState.get(SignBlock.ROTATION) * ONE_SIGN_ROTATION;
            if (MathUtils.isBehindLine(angle, signBlockEntity.getPos().toCenterPos(), cameraPos)) {
                matrixStack.pop();
                ci.cancel();
            }
        }
    }
}
