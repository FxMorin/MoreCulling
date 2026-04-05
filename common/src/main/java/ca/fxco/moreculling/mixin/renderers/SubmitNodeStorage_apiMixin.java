package ca.fxco.moreculling.mixin.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(SubmitNodeStorage.class)
abstract class SubmitNodeStorage_apiMixin implements SubmitNodeCollector {
    @Override
    public void moreculling$submitBlockModelWithoutFace(PoseStack poseStack, RenderType renderType,
                                                        List<BlockStateModelPart> parts, int[] tintLayers,
                                                        int lightCoords, int overlayCoords, int outlineColor,
                                                        Object mesh, Direction withoutFace) {
        order(0).moreculling$submitBlockModelWithoutFace(poseStack, renderType, parts,
                tintLayers, lightCoords, overlayCoords, outlineColor, mesh, withoutFace);
    }

    @Override
    public void moreculling$submitBlockModelForFace(PoseStack poseStack, RenderType renderType,
                                                    List<BlockStateModelPart> parts, int[] tintLayers,
                                                    int lightCoords, int overlayCoords, int outlineColor,
                                                    Object mesh, Direction forFace) {
        order(0).moreculling$submitBlockModelForFace(poseStack, renderType, parts,
                tintLayers, lightCoords, overlayCoords, outlineColor, mesh, forFace);
    }

    @Override
    public void moreculling$submitBlockModelFor3Faces(PoseStack poseStack, RenderType renderType,
                                                      List<BlockStateModelPart> parts, int[] tintLayers,
                                                      int lightCoords, int overlayCoords, int outlineColor,
                                                      Object mesh, Direction faceX, Direction faceY, Direction faceZ) {
        order(0).moreculling$submitBlockModelFor3Faces(poseStack, renderType, parts,
                tintLayers, lightCoords, overlayCoords, outlineColor, mesh, faceX, faceY, faceZ);
    }

    @Override
    public void moreculling$submitBlockModelForFaces(PoseStack poseStack, RenderType renderType,
                                                      List<BlockStateModelPart> parts, int[] tintLayers,
                                                      int lightCoords, int overlayCoords, int outlineColor,
                                                      Object mesh, Direction[] faces) {
        order(0).moreculling$submitBlockModelForFaces(poseStack, renderType, parts,
                tintLayers, lightCoords, overlayCoords, outlineColor, mesh, faces);
    }
}
