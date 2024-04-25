package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.MathUtils;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static ca.fxco.moreculling.utils.CullingUtils.shouldHideWallSignText;
import static ca.fxco.moreculling.utils.MathUtils.ONE_SIGN_ROTATION;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRenderer_textMixin {

    @WrapWithCondition(
            method = "render(Lnet/minecraft/block/entity/SignBlockEntity;" +
                    "Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/block/AbstractSignBlock;" +
                    "Lnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SignBlockEntityRenderer;" +
                            "renderText(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SignText;" +
                            "Lnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumerProvider;IIIZ)V",
                    ordinal = 0
            )
    )
    private boolean moreculling$cullFrontSignText(SignBlockEntityRenderer renderer, BlockPos pos, SignText text,
                                                  MatrixStack matrixStack, VertexConsumerProvider vertexConsumer,
                                                  int i, int j, int i2, boolean l,
                                                  @Local(argsOnly = true) BlockState state,
                                                  @Local(argsOnly = true) Model model) {
        return moreculling$cullSignText(pos, state, model, true);
    }

    @WrapWithCondition(
            method = "render(Lnet/minecraft/block/entity/SignBlockEntity;" +
                    "Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/block/AbstractSignBlock;" +
                    "Lnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SignBlockEntityRenderer;" +
                            "renderText(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SignText;" +
                            "Lnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumerProvider;IIIZ)V",
                    ordinal = 1
            )
    )
    private boolean moreculling$cullBackSignText(SignBlockEntityRenderer renderer, BlockPos pos, SignText text,
                                                 MatrixStack matrixStack, VertexConsumerProvider vertexConsumer,
                                                 int i, int j, int i2, boolean l,
                                                 @Local(argsOnly = true) BlockState state,
                                                 @Local(argsOnly = true) Model model) {
        return moreculling$cullSignText(pos, state, model, false);
    }

    @Unique
    private boolean moreculling$cullSignText(BlockPos pos, BlockState state, Model model, boolean front) {
        if (MoreCulling.CONFIG.signTextCulling) {
            Vec3d cameraPos;
            if (state.contains(WallSignBlock.FACING) &&
                    (cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos()) != null) {
                Direction dir = state.get(WallSignBlock.FACING);
                if (model instanceof SignBlockEntityRenderer.SignModel) {
                    return front == !shouldHideWallSignText(
                            dir,
                            pos.toCenterPos().subtract(dir.getOffsetX() * 0.39, 0, dir.getOffsetZ() * 0.39),
                            cameraPos
                    );
                }
                return front == !shouldHideWallSignText(dir, pos.toCenterPos(), cameraPos);
            }
            cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            double angle = state.get(SignBlock.ROTATION) * ONE_SIGN_ROTATION;
            if (front) { // Switch line orientation xD
                return !MathUtils.isBehindLine(angle, pos.toCenterPos(), cameraPos);
            }
            return !MathUtils.isBehindLine(angle, cameraPos, pos.toCenterPos());
        }
        return true;
    }
}
