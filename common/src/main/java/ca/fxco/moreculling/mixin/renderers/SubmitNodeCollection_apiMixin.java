package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedSubmitNodeCollection;
import ca.fxco.moreculling.api.renderers.modelsubmit.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SubmitNodeCollection.class)
abstract class SubmitNodeCollection_apiMixin implements OrderedSubmitNodeCollector, ExtendedSubmitNodeCollection {
    @Shadow
    private boolean wasUsed;

    @Unique
    private final List<MorecullingBlockModelSubmit> moreculling$BlockModelSubmits = new ArrayList<>();


    @Override
    public void moreculling$submitBlockModelWithoutFace(PoseStack poseStack, RenderType renderType,
                                                        List<BlockStateModelPart> parts, int[] tintLayers,
                                                        int lightCoords, int overlayCoords, int outlineColor,
                                                        Object mesh, Direction withoutFace) {
        wasUsed = true;
        moreculling$BlockModelSubmits.add(new BlockModelSubmitWithoutFace(
                        poseStack.last().copy(),
                        renderType,
                        parts,
                        tintLayers,
                        lightCoords,
                        overlayCoords,
                        outlineColor,
                        mesh,
                        withoutFace
                )
        );
    }

    @Override
    public void moreculling$submitBlockModelForFace(PoseStack poseStack, RenderType renderType,
                                                        List<BlockStateModelPart> parts, int[] tintLayers,
                                                        int lightCoords, int overlayCoords, int outlineColor,
                                                        Object mesh, Direction forFace) {
        wasUsed = true;
        moreculling$BlockModelSubmits.add(new BlockModelSubmitForFace(
                        poseStack.last().copy(),
                        renderType,
                        parts,
                        tintLayers,
                        lightCoords,
                        overlayCoords,
                        outlineColor,
                        mesh,
                forFace
                )
        );
    }

    @Override
    public void moreculling$submitBlockModelFor3Faces(PoseStack poseStack, RenderType renderType,
                                                      List<BlockStateModelPart> parts, int[] tintLayers,
                                                      int lightCoords, int overlayCoords, int outlineColor,
                                                      Object mesh, Direction faceX, Direction faceY, Direction faceZ) {
        wasUsed = true;
        moreculling$BlockModelSubmits.add(new BlockModelSubmitFor3Faces(
                        poseStack.last().copy(),
                        renderType,
                        parts,
                        tintLayers,
                        lightCoords,
                        overlayCoords,
                        outlineColor,
                        mesh,
                        faceX,
                        faceY,
                        faceZ
                )
        );
    }

    @Override
    public void moreculling$submitBlockModelForFaces(PoseStack poseStack, RenderType renderType,
                                                      List<BlockStateModelPart> parts, int[] tintLayers,
                                                      int lightCoords, int overlayCoords, int outlineColor,
                                                      Object mesh, Direction[] faces) {
        wasUsed = true;
        moreculling$BlockModelSubmits.add(new BlockModelSubmitForFaces(
                        poseStack.last().copy(),
                        renderType,
                        parts,
                        tintLayers,
                        lightCoords,
                        overlayCoords,
                        outlineColor,
                        mesh,
                        faces
                )
        );
    }

    @Override
    public List<MorecullingBlockModelSubmit> moreculling$getBlockModelSubmits() {
        return moreculling$BlockModelSubmits;
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void onReturnClear(CallbackInfo ci) {
        moreculling$BlockModelSubmits.clear();
    }
}
