package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.CullingUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaintingRenderer.class)
public abstract class PaintingRenderer_faceCullingMixin {

    @Shadow protected abstract void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float u,
                                           float v, float z, int normalX, int normalY, int normalZ, int packedLight);

    @Inject(
            method = "renderPainting",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void moreculling$cullFace(PoseStack poseStack, VertexConsumer consumer, Painting painting, int width,
                                      int height, TextureAtlasSprite variant,
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
        Direction direction = painting.getDirection();
        Direction opposite = painting.getDirection();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Vertices are drawn from the center, so use negative to offset
                float x1 = negHalfX + (float)x;
                float x2 = negHalfX + (float)(x + 1);
                float y1 = negHalfY + (float)y;
                float y2 = negHalfY + (float)(y + 1);
                int paintingX = painting.getBlockX();
                int paintingY = Mth.floor(painting.getY() + (double)((y1 + y2) / 2.0F));
                int paintingZ = painting.getBlockZ();
                if (direction == Direction.NORTH) {
                    paintingX = Mth.floor(painting.getX() + (double)((x1 + x2) / 2.0F));
                }

                if (direction == Direction.WEST) {
                    paintingZ = Mth.floor(painting.getZ() - (double)((x1 + x2) / 2.0F));
                }

                if (direction == Direction.SOUTH) {
                    paintingX = Mth.floor(painting.getX() - (double)((x1 + x2) / 2.0F));
                }

                if (direction == Direction.EAST) {
                    paintingZ = Mth.floor(painting.getZ() + (double)((x1 + x2) / 2.0F));
                }

                BlockPos paintingPos = new BlockPos(paintingX, paintingY, paintingZ);
                int light = LevelRenderer.getLightColor(painting.level(), paintingPos);
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

                if (!CullingUtils.shouldCullPaintingBack(paintingPos, opposite)) {
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
