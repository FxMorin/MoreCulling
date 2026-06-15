package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.modelsubmit.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SubmitNodeCollection.class)
abstract class SubmitNodeCollection_apiMixin implements OrderedSubmitNodeCollector {

    @Shadow
    @Final
    public TranslucentFeatureRenderPhase translucentBlocksAndItems;
    @Shadow
    @Final
    public SimpleFeatureRenderPhase solid;

    @Shadow
    private static @Nullable RenderType getOutlineRenderType(RenderType renderType) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    @Final
    public SimpleFeatureRenderPhase outline;


    @Override
    public void moreculling$submitBlockModelWithoutFace(PoseStack poseStack, RenderType renderType,
                                                        List<BlockStateModelPart> modelParts, int[] tintLayers,
                                                        int lightCoords, int overlayCoords, int outlineColor,
                                                        Object mesh, Direction withoutFace) {
        PoseStack.Pose pose = poseStack.last().copy();
        if (!renderType.isOutline()) {
            BlockModelSubmitWithoutFace submit = new BlockModelSubmitWithoutFace(
                    pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords, -1, null, mesh, withoutFace
            );
            if (renderType.hasBlending()) {
                this.translucentBlocksAndItems.submit(submit);
            } else {
                this.solid.submit(submit);
            }
        }

        if (outlineColor != 0) {
            RenderType outlineRenderType = getOutlineRenderType(renderType);
            if (outlineRenderType != null) {
                this.outline
                        .submit(
                                new BlockModelSubmitWithoutFace(
                                        pose, outlineRenderType, modelParts, BlockModelRenderState.EMPTY_TINTS,
                                        15728880, OverlayTexture.NO_OVERLAY, outlineColor, null, mesh, withoutFace
                                )
                        );
            }
        }
    }

    @Override
    public void moreculling$submitBlockModelForFace(PoseStack poseStack, RenderType renderType,
                                                        List<BlockStateModelPart> modelParts, int[] tintLayers,
                                                        int lightCoords, int overlayCoords, int outlineColor,
                                                        Object mesh, Direction forFace) {
        PoseStack.Pose pose = poseStack.last().copy();
        if (!renderType.isOutline()) {
            BlockModelSubmitForFace submit = new BlockModelSubmitForFace(
                    pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords, -1, null, mesh, forFace
            );
            if (renderType.hasBlending()) {
                this.translucentBlocksAndItems.submit(submit);
            } else {
                this.solid.submit(submit);
            }
        }

        if (outlineColor != 0) {
            RenderType outlineRenderType = getOutlineRenderType(renderType);
            if (outlineRenderType != null) {
                this.outline
                        .submit(
                                new BlockModelSubmitForFace(
                                        pose, outlineRenderType, modelParts, BlockModelRenderState.EMPTY_TINTS, 15728880, OverlayTexture.NO_OVERLAY, outlineColor, null, mesh, forFace
                                )
                        );
            }
        }
    }

    @Override
    public void moreculling$submitBlockModelFor3Faces(PoseStack poseStack, RenderType renderType,
                                                      List<BlockStateModelPart> modelParts, int[] tintLayers,
                                                      int lightCoords, int overlayCoords, int outlineColor,
                                                      Object mesh, Direction faceX, Direction faceY, Direction faceZ) {
        PoseStack.Pose pose = poseStack.last().copy();
        if (!renderType.isOutline()) {
            BlockModelSubmitFor3Faces submit = new BlockModelSubmitFor3Faces(
                    pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords,
                    -1, null, mesh, faceX, faceY, faceZ
            );
            if (renderType.hasBlending()) {
                this.translucentBlocksAndItems.submit(submit);
            } else {
                this.solid.submit(submit);
            }
        }

        if (outlineColor != 0) {
            RenderType outlineRenderType = getOutlineRenderType(renderType);
            if (outlineRenderType != null) {
                this.outline
                        .submit(
                                new BlockModelSubmitFor3Faces(
                                        pose, outlineRenderType, modelParts,
                                        BlockModelRenderState.EMPTY_TINTS, 15728880,
                                        OverlayTexture.NO_OVERLAY, outlineColor,
                                        null, mesh, faceX, faceY, faceZ
                                )
                        );
            }
        }
    }

    @Override
    public void moreculling$submitBlockModelForFaces(PoseStack poseStack, RenderType renderType,
                                                      List<BlockStateModelPart> modelParts, int[] tintLayers,
                                                      int lightCoords, int overlayCoords, int outlineColor,
                                                      Object mesh, Direction[] faces) {
        PoseStack.Pose pose = poseStack.last().copy();
        if (!renderType.isOutline()) {
            BlockModelSubmitForFaces submit = new BlockModelSubmitForFaces(
                    pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords,
                    -1, null, mesh, faces
            );
            if (renderType.hasBlending()) {
                this.translucentBlocksAndItems.submit(submit);
            } else {
                this.solid.submit(submit);
            }
        }

        if (outlineColor != 0) {
            RenderType outlineRenderType = getOutlineRenderType(renderType);
            if (outlineRenderType != null) {
                this.outline
                        .submit(
                                new BlockModelSubmitForFaces(
                                        pose, outlineRenderType, modelParts,
                                        BlockModelRenderState.EMPTY_TINTS, 15728880,
                                        OverlayTexture.NO_OVERLAY, outlineColor,
                                        null, mesh, faces
                                )
                        );
            }
        }
    }
}
