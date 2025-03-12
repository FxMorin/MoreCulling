package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import ca.fxco.moreculling.utils.TransformationUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ca.fxco.moreculling.utils.DirectionUtils.shiftDirection;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

@Mixin(value = ItemRenderer.class, priority = 1100)
public class ItemRenderer_faceCullingMixin {

    @Inject(
            method = "renderQuadList",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void moreculling$onlySomeFaces$Vanilla(PoseStack poseStack, VertexConsumer buffer,
                                                          List<BakedQuad> quads, int[] tintLayers, int packedLight,
                                                          int packedOverlay, CallbackInfo ci,
                                                          @Local(argsOnly = true) LocalRef<List<BakedQuad>> quadList) {
        if (ItemRendererStates.DIRECTIONS != null) {
            List<BakedQuad> bakedQuads = new ArrayList<>(quads);
            Iterator<BakedQuad> iterator = bakedQuads.iterator();
            quads: while (iterator.hasNext()) {
                BakedQuad bakedQuad = iterator.next();
                Direction face = bakedQuad.direction();
                for (Direction dir : ItemRendererStates.DIRECTIONS) {
                    if (face == dir) {
                        continue quads;
                    }
                }
                iterator.remove();
            }
            quadList.set(bakedQuads);
        }
    }

    @Inject(
            method = "renderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;" +
                            "renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;[III)V"
            )
    )
    private static void moreculling$faceRemoval(ItemDisplayContext itemDisplayContext, PoseStack poseStack,
                                                MultiBufferSource multiBufferSource, int light, int overlay,
                                                int[] tintLayers, List<BakedQuad> bakedModel, RenderType renderType,
                                                ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        ItemFrameRenderState frame = ItemRendererStates.ITEM_FRAME;
        if (frame == null) {
            ItemRendererStates.DIRECTIONS = null;
            return;
        }
        Vec3 cameraPos = ItemRendererStates.CAMERA.getPosition();
        Vec3 framePos = new Vec3(frame.x, frame.y, frame.z);
        boolean isBlockItem = false;//TODO!((BakedOpacity) bakedModel).moreculling$isItem();
        ItemTransform transformation = ItemRendererStates.TRANSFORMS;
        boolean canCull = ((!isBlockItem && !frame.isInvisible) || CullingUtils.shouldCullBack(frame)) &&
                TransformationUtils.canCullTransformation(transformation);
        double dist = ItemRendererStates.CAMERA.getPosition().distanceTo(framePos);
        // Make blocks use LOD - If more than range, only render the front and maybe back if it can't cull
        if (isBlockItem && dist <= 3) { // 3 Blocks away
            ItemRendererStates.DIRECTIONS = null;
        } else if (MoreCulling.CONFIG.useItemFrameLOD && !isBlockItem && dist > MoreCulling.CONFIG.itemFrameLODRange) {
            if (!canCull) {
                ItemRendererStates.DIRECTIONS = new Direction[] { SOUTH, NORTH };
            } else {
                ItemRendererStates.DIRECTIONS = new Direction[] { SOUTH };
            }
        } else {
            // EXPERIMENTAL CULLING
            // Use smart culling to render only 3 face directions.
            // TODO: Add model rotation logic (items need this!) Currently we only support blocks and some models
            if (MoreCulling.CONFIG.useItemFrame3FaceCulling &&
                    dist > MoreCulling.CONFIG.itemFrame3FaceCullingRange &&
                    frame.rotation % 2 == 0 &&
                    transformation.rotation().y() == 0 &&
                    transformation.rotation().x() == 0 &&
                    transformation.rotation().z() == 0
            ) {
                int rotation = frame.rotation * 45;
                Direction facing = frame.direction;
                Direction dirX = shiftDirection(facing,
                        cameraPos.x > framePos.x ? Direction.EAST : Direction.WEST, rotation);
                Direction dirY = shiftDirection(facing,
                        cameraPos.y > framePos.y ? Direction.UP : Direction.DOWN, rotation);
                Direction dirZ = shiftDirection(facing,
                        cameraPos.z > framePos.z ? SOUTH : NORTH, rotation);
                ItemRendererStates.DIRECTIONS = new Direction[] { dirX, dirY, dirZ };
            } else {
                if (canCull) {
                    ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(
                            DirectionUtils.changeDirectionUsingTransformation(NORTH, transformation)
                    );
                } else {
                    ItemRendererStates.DIRECTIONS = null;
                }
            }
        }
    }
}
