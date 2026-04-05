package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.List;

@Restriction(require = @Condition("fabric-renderer-api-v1"))
@Mixin(value = BlockModelRenderState.class, priority = 1200)
public abstract class BlockModelRenderState_fabricCullMixin implements ExtendedBlockModelRenderState {

    @Shadow
    private @org.jspecify.annotations.Nullable List<BlockStateModelPart> modelParts;
    @Shadow
    private @org.jspecify.annotations.Nullable IntList tintLayers;
    @Shadow
    private @org.jspecify.annotations.Nullable Matrix4fc transformation;
    @Shadow
    @Final
    public static int[] EMPTY_TINTS;
    @Shadow
    private MutableMesh mesh;


    @Override
    public void moreculling$submitModelWithoutFace(RenderType renderType, PoseStack poseStack,
                                                   SubmitNodeCollector submitNodeCollector, int lightCoords,
                                                   int overlayCoords, int outlineColor, Direction withoutFace) {
        if (mesh != null && mesh.size() > 0) {
            List<BlockStateModelPart> modelPartsCopy = modelParts != null && !modelParts.isEmpty()
                    ? new ObjectArrayList<>(modelParts) : Collections.emptyList();
            Mesh meshCopy = mesh.immutableCopy();
            int[] tints = tintLayers != null ? tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;

            if (transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(transformation);
                submitNodeCollector.moreculling$submitBlockModelWithoutFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, withoutFace);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelWithoutFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, withoutFace);
            }

            return;
        }
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(this.modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.moreculling$submitBlockModelWithoutFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, withoutFace);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelWithoutFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, withoutFace);
            }
        }
    }

    @Override
    public void moreculling$submitModelForFace(RenderType renderType, PoseStack poseStack,
                                                   SubmitNodeCollector submitNodeCollector, int lightCoords,
                                                   int overlayCoords, int outlineColor, Direction forFace) {
        if (mesh != null && mesh.size() > 0) {
            List<BlockStateModelPart> modelPartsCopy = modelParts != null && !modelParts.isEmpty()
                    ? new ObjectArrayList<>(modelParts) : Collections.emptyList();
            Mesh meshCopy = mesh.immutableCopy();
            int[] tints = tintLayers != null ? tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;

            if (transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(transformation);
                submitNodeCollector.moreculling$submitBlockModelForFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, forFace);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelForFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, forFace);
            }

            return;
        }
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(this.modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.moreculling$submitBlockModelForFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, forFace);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelForFace(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, forFace);
            }
        }
    }

    @Override
    public void moreculling$submitModelFor3Faces(RenderType renderType, PoseStack poseStack,
                                                   SubmitNodeCollector submitNodeCollector, int lightCoords,
                                                   int overlayCoords, int outlineColor,
                                                 Direction faceX, Direction faceY, Direction faceZ) {
        if (mesh != null && mesh.size() > 0) {
            List<BlockStateModelPart> modelPartsCopy = modelParts != null && !modelParts.isEmpty()
                    ? new ObjectArrayList<>(modelParts) : Collections.emptyList();
            Mesh meshCopy = mesh.immutableCopy();
            int[] tints = tintLayers != null ? tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;

            if (transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(transformation);
                submitNodeCollector.moreculling$submitBlockModelFor3Faces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, faceX, faceY, faceZ);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelFor3Faces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, faceX, faceY, faceZ);
            }

            return;
        }
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(this.modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.moreculling$submitBlockModelFor3Faces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, faceX, faceY, faceZ);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelFor3Faces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, faceX, faceY, faceZ);
            }
        }
    }

    @Override
    public void moreculling$submitModelForFaces(RenderType renderType, PoseStack poseStack,
                                                   SubmitNodeCollector submitNodeCollector, int lightCoords,
                                                   int overlayCoords, int outlineColor, Direction[] faces) {
        if (mesh != null && mesh.size() > 0) {
            List<BlockStateModelPart> modelPartsCopy = modelParts != null && !modelParts.isEmpty()
                    ? new ObjectArrayList<>(modelParts) : Collections.emptyList();
            Mesh meshCopy = mesh.immutableCopy();
            int[] tints = tintLayers != null ? tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;

            if (transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(transformation);
                submitNodeCollector.moreculling$submitBlockModelForFaces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, faces);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelForFaces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, meshCopy, faces);
            }

            return;
        }
        if (this.modelParts != null && !this.modelParts.isEmpty()) {
            List<BlockStateModelPart> modelPartsCopy = new ObjectArrayList<>(this.modelParts);
            int[] tints = this.tintLayers != null ? this.tintLayers.toArray(EMPTY_TINTS) : EMPTY_TINTS;
            if (this.transformation != null) {
                poseStack.pushPose();
                poseStack.mulPose(this.transformation);
                submitNodeCollector.moreculling$submitBlockModelForFaces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, faces);
                poseStack.popPose();
            } else {
                submitNodeCollector.moreculling$submitBlockModelForFaces(poseStack, renderType,
                        modelPartsCopy, tints, lightCoords, overlayCoords, outlineColor, null, faces);
            }
        }
    }
}
