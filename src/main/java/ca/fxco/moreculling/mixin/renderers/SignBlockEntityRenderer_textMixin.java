package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static ca.fxco.moreculling.utils.CullingUtils.shouldHideWallSignText;
import static ca.fxco.moreculling.utils.MathUtils.ONE_SIGN_ROTATION;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRenderer_textMixin {

    @Inject(
            method = "render(Lnet/minecraft/block/entity/SignBlockEntity;" +
                    "FLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void cullSignText(SignBlockEntity signBlockEntity, float tickDelta, MatrixStack matrixStack,
                              VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci,
                              BlockState blockState, float g, SignType signType,
                              SignBlockEntityRenderer.SignModel signModel) {
        if (MoreCulling.CONFIG.signTextCulling) {
            Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            if (signModel.stick.visible) {
                double angle = blockState.get(SignBlock.ROTATION) * ONE_SIGN_ROTATION;
                if (MathUtils.isBehindLine(angle, Vec3d.ofCenter(signBlockEntity.getPos()), cameraPos)) {
                    matrixStack.pop();
                    ci.cancel();
                }
            } else { // wall mounted
                Direction dir = blockState.get(WallSignBlock.FACING);
                if (shouldHideWallSignText(
                        dir,
                        Vec3d.ofCenter(signBlockEntity.getPos())
                                .subtract(dir.getOffsetX() * 0.39, 0, dir.getOffsetZ() * 0.39),
                        cameraPos
                )) {
                    matrixStack.pop();
                    ci.cancel();
                }
            }
        }
    }
}
