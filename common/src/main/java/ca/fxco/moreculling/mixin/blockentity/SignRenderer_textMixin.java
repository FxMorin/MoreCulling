package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.MathUtils;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static ca.fxco.moreculling.utils.CullingUtils.shouldHideWallSignText;
import static ca.fxco.moreculling.utils.MathUtils.ONE_SIGN_ROTATION;

@Mixin(AbstractSignRenderer.class)
public class SignRenderer_textMixin {

    @WrapWithCondition(
            method = "submitSignWithText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/AbstractSignRenderer;" +
                            "submitSignText(Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Z)V",
                    ordinal = 0
            )
    )
    private boolean moreculling$cullFrontSignText(AbstractSignRenderer instance, SignRenderState signRenderState,
                                                  PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                                  boolean bl, @Local(argsOnly = true) BlockState state,
                                                  @Local(argsOnly = true) Model.Simple model) {
        return moreculling$cullSignText(signRenderState.blockPos, state, model, true);
    }

    @WrapWithCondition(
            method = "submitSignWithText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/AbstractSignRenderer;" +
            "submitSignText(Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;" +
            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Z)V",
                    ordinal = 1
            )
    )
    private boolean moreculling$cullBackSignText(AbstractSignRenderer instance, SignRenderState signRenderState,
                                                 PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                                 boolean bl, @Local(argsOnly = true) BlockState state,
                                                 @Local(argsOnly = true) Model.Simple model) {
        return moreculling$cullSignText(signRenderState.blockPos, state, model, false);
    }

    @Unique
    private boolean moreculling$cullSignText(BlockPos pos, BlockState state, Model model, boolean front) {
        if (MoreCulling.CONFIG.signTextCulling) {
            Vec3 cameraPos;
            if (state.hasProperty(WallSignBlock.FACING) &&
                    (cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().position()) != null) {
                Direction dir = state.getValue(WallSignBlock.FACING);
                if (model instanceof Model.Simple) {
                    return front == !shouldHideWallSignText(
                            dir,
                            pos.getCenter().subtract(dir.getStepX() * 0.39, 0, dir.getStepZ() * 0.39),
                            cameraPos
                    );
                }
                return front == !shouldHideWallSignText(dir, pos.getCenter(), cameraPos);
            }
            cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
            double angle = state.getValue(StandingSignBlock.ROTATION) * ONE_SIGN_ROTATION;
            if (front) { // Switch line orientation xD
                return !MathUtils.isBehindLine(angle, pos.getCenter(), cameraPos);
            }
            return !MathUtils.isBehindLine(angle, cameraPos, pos.getCenter());
        }
        return true;
    }
}
