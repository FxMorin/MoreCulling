package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderState;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;

@Mixin(BlockModelRenderState.class)
public abstract class BlockModelRenderState_cullMixin implements ExtendedBlockModelRenderState {
    @Unique
    private static final QuadInstance moreculling$quadInstance = new QuadInstance();

    @Shadow
    private @org.jspecify.annotations.Nullable List<BlockStateModelPart> modelParts;
    @Shadow
    private @org.jspecify.annotations.Nullable IntList tintLayers;
    @Shadow
    private @org.jspecify.annotations.Nullable Matrix4fc transformation;
    @Shadow
    @Final
    public static int[] EMPTY_TINTS;

    @Override
    public void moreculling$renderQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, int[] tintLayers, VertexConsumer buffer) {
        int tintIndex = quad.materialInfo().tintIndex();
        boolean tintColor = tintIndex != -1 && tintIndex < tintLayers.length;
        instance.setColor(tintColor ? tintLayers[tintIndex] : -1);
        buffer.putBakedQuad(pose, quad, instance);
    }

    @Override
    public void moreculling$renderModelWithoutFace(PoseStack.Pose pose, VertexConsumer vertices,
                                                   List<BlockStateModelPart> model, int[] tintLayers, int light,
                                                   int overlay, Direction withoutFace) {
        moreculling$quadInstance.setLightCoords(light);
        moreculling$quadInstance.setOverlayCoords(overlay);
        for (BlockStateModelPart part : model) {
            for (Direction direction : DirectionUtils.DIRECTIONS) {
                if (direction == withoutFace) {
                    continue;
                }
                List<BakedQuad> bakedQuads = part.getQuads(direction);
                if (!bakedQuads.isEmpty()) {
                    moreculling$renderQuads(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance);
                }
            }
            List<BakedQuad> bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsWithoutFace(pose, vertices, tintLayers,
                        bakedQuads, moreculling$quadInstance, withoutFace);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsWithoutFace(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                                   List<BakedQuad> quads, QuadInstance instance,
                                                   Direction withoutFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.direction() == withoutFace) {
                continue;
            }
            moreculling$renderQuad(pose, bakedQuad, instance, tintLayers, vertices);
        }
    }

    @Override
    public void moreculling$submitModelWithoutFace(RenderType renderType, PoseStack poseStack,
                                                   SubmitNodeCollector submitNodeCollector, int lightCoords,
                                                   int overlayCoords, int outlineColor, Direction withoutFace) {
        moreculling$quadInstance.setLightCoords(lightCoords);
        moreculling$quadInstance.setOverlayCoords(overlayCoords);
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                    moreculling$renderModelWithoutFace(
                            pose,
                            buffer,
                            modelPartsCopy,
                            tints,
                            lightCoords,
                            overlayCoords,
                            withoutFace
                    );
                });
                poseStack.popPose();
            } else {
                submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                    moreculling$renderModelWithoutFace(
                            pose,
                            buffer,
                            modelPartsCopy,
                            tints,
                            lightCoords,
                            overlayCoords,
                            withoutFace
                    );
                });
            }
        }
    }

    @Override
    public void moreculling$renderModelForFace(PoseStack.Pose pose, VertexConsumer vertices,
                                               List<BlockStateModelPart> model, int[] tintLayers,
                                               int light, int overlay, Direction forFace) {
        moreculling$quadInstance.setLightCoords(light);
        moreculling$quadInstance.setOverlayCoords(overlay);
        for (BlockStateModelPart part : model) {
            List<BakedQuad> bakedQuads = part.getQuads(forFace);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance);
            }
            bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsForFace(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance, forFace);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsForFace(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                               List<BakedQuad> quads, QuadInstance instance, Direction forFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.direction() != forFace) {
                continue;
            }
            moreculling$renderQuad(pose, bakedQuad, instance, tintLayers, vertices);
        }
    }

    @Override
    public void moreculling$renderModelFor3Faces(PoseStack.Pose pose, VertexConsumer vertices,
                                                 List<BlockStateModelPart> model, int[] tintLayers,
                                                 int light, int overlay, Direction faceX,
                                                 Direction faceY, Direction faceZ) {
        moreculling$quadInstance.setLightCoords(light);
        moreculling$quadInstance.setOverlayCoords(overlay);
        for (BlockStateModelPart part : model) {
            List<BakedQuad> bakedQuads = part.getQuads(faceX);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance);
            }
            bakedQuads = part.getQuads(faceY);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance);
            }
            bakedQuads = part.getQuads(faceZ);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance);
            }
            bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsFor3Faces(pose, vertices, tintLayers,
                        bakedQuads, moreculling$quadInstance, faceX, faceY, faceZ);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsFor3Faces(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                                 List<BakedQuad> quads, QuadInstance instance,
                                                 Direction faceX, Direction faceY, Direction faceZ) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.direction();
            if (face == faceX || face == faceY || face == faceZ) {
                moreculling$renderQuad(pose, bakedQuad, instance, tintLayers, vertices);
            }
        }
    }

    @Override
    public void moreculling$renderModelForFaces(PoseStack.Pose pose, VertexConsumer vertices,
                                                List<BlockStateModelPart> model, int[] tintLayers,
                                                int light, int overlay, Direction[] faces) {
        moreculling$quadInstance.setLightCoords(light);
        moreculling$quadInstance.setOverlayCoords(overlay);
        for (BlockStateModelPart part : model) {
            for (Direction direction : faces) {
                List<BakedQuad> bakedQuads = part.getQuads(direction);
                if (!bakedQuads.isEmpty()) {
                    moreculling$renderQuads(pose, vertices, tintLayers, bakedQuads, moreculling$quadInstance);
                }
            }

            List<BakedQuad> bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsForFaces(pose, vertices, tintLayers,
                        bakedQuads, moreculling$quadInstance, faces);
            }
        }
    }

    @Override
    public void moreculling$submitModelForFaces(RenderType renderType, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor, Direction[] faces) {
        moreculling$quadInstance.setLightCoords(lightCoords);
        moreculling$quadInstance.setOverlayCoords(overlayCoords);
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                    moreculling$renderModelForFaces(
                            pose,
                            buffer,
                            modelPartsCopy,
                            tints,
                            lightCoords,
                            overlayCoords,
                            faces
                    );
                });
                poseStack.popPose();
            } else {
                submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                    moreculling$renderModelForFaces(
                            pose,
                            buffer,
                            modelPartsCopy,
                            tints,
                            lightCoords,
                            overlayCoords,
                            faces
                    );
                });
            }
        }
    }

    @Override
    public void moreculling$renderQuadsForFaces(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                                List<BakedQuad> quads, QuadInstance instance, Direction[] faces) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.direction();
            if (Arrays.stream(faces).anyMatch((f) -> f == face)) {
                moreculling$renderQuad(pose, bakedQuad, instance, tintLayers, vertices);
            }
        }
    }

    @Unique
    private void moreculling$renderQuads(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                         List<BakedQuad> quads, QuadInstance instance) {
        for (BakedQuad bakedQuad : quads) {
            moreculling$renderQuad(pose, bakedQuad, instance, tintLayers, vertices);
        }
    }
}
