package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

import java.util.List;

public interface ExtendedOrderedSubmitNodeCollector {
    default void moreculling$submitBlockModelWithoutFace(PoseStack poseStack, RenderType renderType,
                                                         List<BlockStateModelPart> parts, int[] tintLayers,
                                                         int lightCoords, int overlayCoords, int outlineColor,
                                                         Object mesh, Direction withoutFace) {
        ((OrderedSubmitNodeCollector) this).submitBlockModel(poseStack, renderType,
                parts, tintLayers, lightCoords, overlayCoords, outlineColor);
    }

    default void moreculling$submitBlockModelForFace(PoseStack poseStack, RenderType renderType,
                                                         List<BlockStateModelPart> parts, int[] tintLayers,
                                                         int lightCoords, int overlayCoords, int outlineColor,
                                                         Object mesh, Direction forFace) {
        ((OrderedSubmitNodeCollector) this).submitBlockModel(poseStack, renderType,
                parts, tintLayers, lightCoords, overlayCoords, outlineColor);
    }

    default void moreculling$submitBlockModelFor3Faces(PoseStack poseStack, RenderType renderType,
                                                       List<BlockStateModelPart> parts, int[] tintLayers,
                                                       int lightCoords, int overlayCoords, int outlineColor,
                                                       Object mesh, Direction faceX, Direction faceY, Direction faceZ) {
        ((OrderedSubmitNodeCollector) this).submitBlockModel(poseStack, renderType,
                parts, tintLayers, lightCoords, overlayCoords, outlineColor);
    }

    default void moreculling$submitBlockModelForFaces(PoseStack poseStack, RenderType renderType,
                                                       List<BlockStateModelPart> parts, int[] tintLayers,
                                                       int lightCoords, int overlayCoords, int outlineColor,
                                                       Object mesh, Direction[] faces) {
        ((OrderedSubmitNodeCollector) this).submitBlockModel(poseStack, renderType,
                parts, tintLayers, lightCoords, overlayCoords, outlineColor);
    }
}
