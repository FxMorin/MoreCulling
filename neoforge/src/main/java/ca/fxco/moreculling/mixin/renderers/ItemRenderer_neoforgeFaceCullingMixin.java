package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import ca.fxco.moreculling.utils.TransformationUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.fxco.moreculling.utils.DirectionUtils.shiftDirection;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

@Mixin(value = ItemRenderer.class, priority = 1100)
public class ItemRenderer_neoforgeFaceCullingMixin {

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/ClientHooks;handleCameraTransforms(" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/resources/model/BakedModel;" +
                            "Lnet/minecraft/world/item/ItemDisplayContext" +
                            ";Z)Lnet/minecraft/client/resources/model/BakedModel;"
            )
    )
    private BakedModel moreculling$getTransformation(
            PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean applyLeftHandTransform,
            Operation<BakedModel> original, @Share("transformation") LocalRef<ItemTransform> transformationRef
    ) {
        ItemTransform transformation = model.getTransforms().getTransform(cameraTransformType);
        if (ItemRendererStates.ITEM_FRAME != null) {
            transformationRef.set(transformation);
        }
        return original.call(poseStack, model, cameraTransformType, applyLeftHandTransform);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(" +
                            "Lnet/minecraft/client/resources/model/BakedModel;" +
                            "Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void moreculling$faceRemoval(ItemStack stack, ItemDisplayContext displayContext,
                                         boolean leftHanded, PoseStack poseStack,
                                         MultiBufferSource multiBufferSource, int light, int overlay,
                                         BakedModel model, CallbackInfo ci,
                                         @Share("transformation") LocalRef<ItemTransform> transformationRef) {
        ItemFrame frame = ItemRendererStates.ITEM_FRAME;
        if (frame == null) {
            ItemRendererStates.DIRECTIONS = null;
            return;
        }
        Vec3 cameraPos = ItemRendererStates.CAMERA.getPosition();
        Vec3 framePos = frame.position();
        boolean isBlockItem = stack.getItem() instanceof BlockItem;
        ItemTransform transformation = transformationRef.get();
        boolean canCull = ((!isBlockItem && !frame.isInvisible()) || CullingUtils.shouldCullBack(frame)) &&
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
                    frame.getRotation() % 2 == 0 &&
                    transformation.rotation.y() == 0 &&
                    transformation.rotation.x() == 0 &&
                    transformation.rotation.z() == 0
            ) {
                int rotation = frame.getRotation() * 45;
                Direction facing = frame.getDirection();
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
