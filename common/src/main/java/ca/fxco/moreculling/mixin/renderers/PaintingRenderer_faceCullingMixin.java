package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.MoreCullingConfig;
import ca.fxco.moreculling.utils.CullingUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.Painting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.fxco.moreculling.states.PaintingRendererStates.DIRECTION;
import static ca.fxco.moreculling.states.PaintingRendererStates.PAINTING_POS;

@Mixin(PaintingRenderer.class)
public abstract class PaintingRenderer_faceCullingMixin {
    @Shadow protected abstract void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int packedLight);

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/decoration/Painting;" +
                    "Lnet/minecraft/client/renderer/entity/state/PaintingRenderState;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/Painting;level()Lnet/minecraft/world/level/Level;"
            )
    )
    public void getPaintingPos(Painting painting, PaintingRenderState p_364263_, float p_360460_, CallbackInfo ci,
                               @Local(ordinal = 0) int width, @Local(ordinal = 1) int height) {
        PAINTING_POS = new BlockPos[width][height];
        DIRECTION = painting.getDirection();
    }

    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/world/entity/decoration/Painting;" +
                    "Lnet/minecraft/client/renderer/entity/state/PaintingRenderState;F)V",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/core/BlockPos;")
    )
    public BlockPos moreculling$getPaintingPos(int x, int y, int z, Operation<BlockPos> original,
                                   @Local(ordinal = 3) int width, @Local(ordinal = 2) int height) {
        BlockPos pos = original.call(x, y, z);
        PAINTING_POS[width][height] = pos;
        return pos;
    }



    @Inject(
            method = "renderPainting",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void moreculling$cullFace(PoseStack p_115559_, VertexConsumer p_115560_, int[] p_363896_, int width, int height, TextureAtlasSprite p_115564_, TextureAtlasSprite p_115565_, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.paintingCulling) {
            return;
        }
        PoseStack.Pose posestack$pose = p_115559_.last();
        float f = (float)(-width) / 2.0F;
        float f1 = (float)(-height) / 2.0F;
        float f2 = 0.03125F;
        float f3 = p_115565_.getU0();
        float f4 = p_115565_.getU1();
        float f5 = p_115565_.getV0();
        float f6 = p_115565_.getV1();
        float f7 = p_115565_.getU0();
        float f8 = p_115565_.getU1();
        float f9 = p_115565_.getV0();
        float f10 = p_115565_.getV(0.0625F);
        float f11 = p_115565_.getU0();
        float f12 = p_115565_.getU(0.0625F);
        float f13 = p_115565_.getV0();
        float f14 = p_115565_.getV1();
        double d0 = 1.0 / (double)width;
        double d1 = 1.0 / (double)height;
        Direction opposite = DIRECTION.getOpposite();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float f15 = f + (float)(i + 1);
                float f16 = f + (float)i;
                float f17 = f1 + (float)(j + 1);
                float f18 = f1 + (float)j;
                int k = p_363896_[i + j * width];
                float f19 = p_115564_.getU((float)(d0 * (double)(width - i)));
                float f20 = p_115564_.getU((float)(d0 * (double)(width - (i + 1))));
                float f21 = p_115564_.getV((float)(d1 * (double)(height - j)));
                float f22 = p_115564_.getV((float)(d1 * (double)(height - (j + 1))));
                //front
                this.vertex(posestack$pose, p_115560_, f15, f18, f20, f21, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f19, f21, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f19, f22, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f20, f22, -0.03125F, 0, 0, -1, k);

                if (!CullingUtils.shouldCullPaintingBack(PAINTING_POS[i][j], opposite)) {
                    //back
                    this.vertex(posestack$pose, p_115560_, f15, f17, f4, f5, 0.03125F, 0, 0, 1, k);
                    this.vertex(posestack$pose, p_115560_, f16, f17, f3, f5, 0.03125F, 0, 0, 1, k);
                    this.vertex(posestack$pose, p_115560_, f16, f18, f3, f6, 0.03125F, 0, 0, 1, k);
                    this.vertex(posestack$pose, p_115560_, f15, f18, f4, f6, 0.03125F, 0, 0, 1, k);
                }

                if (j == height - 1) {
                    //up
                    this.vertex(posestack$pose, p_115560_, f15, f17, f7, f9, -0.03125F, 0, 1, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f17, f8, f9, -0.03125F, 0, 1, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f17, f8, f10, 0.03125F, 0, 1, 0, k);
                    this.vertex(posestack$pose, p_115560_, f15, f17, f7, f10, 0.03125F, 0, 1, 0, k);
                }

                if (j == 0) {
                    //down
                    this.vertex(posestack$pose, p_115560_, f15, f18, f7, f9, 0.03125F, 0, -1, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f18, f8, f9, 0.03125F, 0, -1, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f18, f8, f10, -0.03125F, 0, -1, 0, k);
                    this.vertex(posestack$pose, p_115560_, f15, f18, f7, f10, -0.03125F, 0, -1, 0, k);
                }

                if (i == width - 1) {
                    //left
                    this.vertex(posestack$pose, p_115560_, f15, f17, f12, f13, 0.03125F, -1, 0, 0, k);
                    this.vertex(posestack$pose, p_115560_, f15, f18, f12, f14, 0.03125F, -1, 0, 0, k);
                    this.vertex(posestack$pose, p_115560_, f15, f18, f11, f14, -0.03125F, -1, 0, 0, k);
                    this.vertex(posestack$pose, p_115560_, f15, f17, f11, f13, -0.03125F, -1, 0, 0, k);
                }

                if (i == 0) {
                    //right
                    this.vertex(posestack$pose, p_115560_, f16, f17, f12, f13, -0.03125F, 1, 0, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f18, f12, f14, -0.03125F, 1, 0, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f18, f11, f14, 0.03125F, 1, 0, 0, k);
                    this.vertex(posestack$pose, p_115560_, f16, f17, f11, f13, 0.03125F, 1, 0, 0, k);
                }
            }
        }
        ci.cancel();
    }
}
