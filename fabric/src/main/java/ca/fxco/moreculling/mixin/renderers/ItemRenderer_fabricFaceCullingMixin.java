package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import ca.fxco.moreculling.utils.TransformationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.fxco.moreculling.utils.DirectionUtils.shiftDirection;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

@Mixin(value = ItemRenderer.class, priority = 1100)
public class ItemRenderer_fabricFaceCullingMixin {

    @Inject(
            method = "renderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(" +
                            "Lnet/minecraft/client/resources/model/BakedModel;" +
                            "[IIILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private static void moreculling$faceRemoval(ItemDisplayContext itemDisplayContext, PoseStack poseStack,
                                                MultiBufferSource multiBufferSource, int light, int overlay, int[] is,
                                                BakedModel bakedModel, RenderType renderType,
                                                ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        ItemFrameRenderState frame = ItemRendererStates.ITEM_FRAME;
        if (frame == null) {
            ItemRendererStates.DIRECTIONS = null;
            return;
        }
        Vec3 cameraPos = ItemRendererStates.CAMERA.getPosition();
        Vec3 framePos = new Vec3(frame.x, frame.y, frame.z) ;
        ItemTransform transformation = ItemRendererStates.TRANSFORMS;
        if (transformation == null)
            return;
        boolean canCull = (CullingUtils.shouldCullBack(frame)) &&
                TransformationUtils.canCullTransformation(transformation);
        double dist = ItemRendererStates.CAMERA.getPosition().distanceTo(framePos);
        // Make blocks use LOD - If more than range, only render the front and maybe back if it can't cull
        if (dist <= 3) { // 3 Blocks away
            ItemRendererStates.DIRECTIONS = null;
        } else if (MoreCulling.CONFIG.useItemFrameLOD && dist > MoreCulling.CONFIG.itemFrameLODRange) {
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
                    transformation.rotation.y() == 0 &&
                    transformation.rotation.x() == 0 &&
                    transformation.rotation.z() == 0
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
