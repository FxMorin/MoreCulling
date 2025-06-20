package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.CullingUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
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

@Restriction(conflict = {
    @Condition("smoothmaps")
})
@Mixin(PaintingRenderer.class)
public abstract class PaintingRenderer_faceCullingMixin {

    @Shadow protected abstract void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u,
                                           float v, float z, int normalX, int normalY, int normalZ, int packedLight);

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/decoration/Painting;" +
                    "Lnet/minecraft/client/renderer/entity/state/PaintingRenderState;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/Painting;level()Lnet/minecraft/world/level/Level;"
            )
    )
    private void moreculling$getPaintingPos(Painting painting, PaintingRenderState renderState, float partialTick,
                                            CallbackInfo ci, @Local(ordinal = 0) int width,
                                            @Local(ordinal = 1) int height) {
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
    private BlockPos moreculling$getPaintingPos(int x, int y, int z, Operation<BlockPos> original,
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
    private void moreculling$cullFace(PoseStack poseStack, VertexConsumer consumer, int[] lightCoords,
                                      int width, int height, TextureAtlasSprite variant,
                                      TextureAtlasSprite paintingAtlas, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.paintingCulling) {
            return;
        }
        PoseStack.Pose pose = poseStack.last();
        float negHalfX = (float)(-width) / 2.0F;
        float negHalfY = (float)(-height) / 2.0F;
        float u0 = paintingAtlas.getU0();
        float u1 = paintingAtlas.getU1();
        float v0 = paintingAtlas.getV0();
        float v1 = paintingAtlas.getV1();
        float vY = paintingAtlas.getV(0.0625F);
        float uY = paintingAtlas.getU(0.0625F);
        double d0 = 1.0 / (double)width;  // fast math
        double d1 = 1.0 / (double)height; // fast math
        Direction opposite = DIRECTION.getOpposite();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Vertices are drawn from the center, so use negative to offset
                float x1 = negHalfX + (float)x;
                float x2 = negHalfX + (float)(x + 1);
                float y1 = negHalfY + (float)y;
                float y2 = negHalfY + (float)(y + 1);
                int light = lightCoords[x + y * width];
                // Get the UV's for a single block of the texture instead of the entire texture
                float fU0 = variant.getU((float)(d0 * (double)(width - x)));        // Front U0
                float fU1 = variant.getU((float)(d0 * (double)(width - (x + 1))));  // Front U1
                float fV0 = variant.getV((float)(d1 * (double)(height - y)));       // Front V0
                float fV1 = variant.getV((float)(d1 * (double)(height - (y + 1)))); // Front V1
                //front
                this.vertex(pose, consumer, x2, y1, fU1, fV0, -0.03125F, 0, 0, -1, light);
                this.vertex(pose, consumer, x1, y1, fU0, fV0, -0.03125F, 0, 0, -1, light);
                this.vertex(pose, consumer, x1, y2, fU0, fV1, -0.03125F, 0, 0, -1, light);
                this.vertex(pose, consumer, x2, y2, fU1, fV1, -0.03125F, 0, 0, -1, light);

                if (!CullingUtils.shouldCullPaintingBack(PAINTING_POS[x][y], opposite)) {
                    //back
                    this.vertex(pose, consumer, x2, y2, u1, v0, 0.03125F, 0, 0, 1, light);
                    this.vertex(pose, consumer, x1, y2, u0, v0, 0.03125F, 0, 0, 1, light);
                    this.vertex(pose, consumer, x1, y1, u0, v1, 0.03125F, 0, 0, 1, light);
                    this.vertex(pose, consumer, x2, y1, u1, v1, 0.03125F, 0, 0, 1, light);
                }

                if (y == height - 1) {
                    //up
                    this.vertex(pose, consumer, x2, y2, u0, v0, -0.03125F, 0, 1, 0, light);
                    this.vertex(pose, consumer, x1, y2, u1, v0, -0.03125F, 0, 1, 0, light);
                    this.vertex(pose, consumer, x1, y2, u1, vY, 0.03125F, 0, 1, 0, light);
                    this.vertex(pose, consumer, x2, y2, u0, vY, 0.03125F, 0, 1, 0, light);
                }

                if (y == 0) {
                    //down
                    this.vertex(pose, consumer, x2, y1, u0, v0, 0.03125F, 0, -1, 0, light);
                    this.vertex(pose, consumer, x1, y1, u1, v0, 0.03125F, 0, -1, 0, light);
                    this.vertex(pose, consumer, x1, y1, u1, vY, -0.03125F, 0, -1, 0, light);
                    this.vertex(pose, consumer, x2, y1, u0, vY, -0.03125F, 0, -1, 0, light);
                }

                if (x == width - 1) {
                    //left
                    this.vertex(pose, consumer, x2, y2, uY, v0, 0.03125F, -1, 0, 0, light);
                    this.vertex(pose, consumer, x2, y1, uY, v1, 0.03125F, -1, 0, 0, light);
                    this.vertex(pose, consumer, x2, y1, u0, v1, -0.03125F, -1, 0, 0, light);
                    this.vertex(pose, consumer, x2, y2, u0, v0, -0.03125F, -1, 0, 0, light);
                }

                if (x == 0) {
                    //right
                    this.vertex(pose, consumer, x1, y2, uY, v0, -0.03125F, 1, 0, 0, light);
                    this.vertex(pose, consumer, x1, y1, uY, v1, -0.03125F, 1, 0, 0, light);
                    this.vertex(pose, consumer, x1, y1, u0, v1, 0.03125F, 1, 0, 0, light);
                    this.vertex(pose, consumer, x1, y2, u0, v0, 0.03125F, 1, 0, 0, light);
                }
            }
        }
        ci.cancel();
    }
}
