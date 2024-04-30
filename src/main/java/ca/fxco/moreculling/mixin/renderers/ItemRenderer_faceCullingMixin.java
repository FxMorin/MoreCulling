package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import ca.fxco.moreculling.utils.TransformationUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ca.fxco.moreculling.utils.DirectionUtils.shiftDirection;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

@Mixin(value = ItemRenderer.class, priority = 1100)
public class ItemRenderer_faceCullingMixin {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemDisplayContext;firstPerson()Z",
                    ordinal = 0
            )
    )
    private boolean moreculling$skipSlowTransparencyChecks(ItemDisplayContext renderMode) {
        return ItemRendererStates.ITEM_FRAME != null;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderType(" +
                            "Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/client/renderer/RenderType;"
            )
    )
    private void moreculling$fastTransparencyChecks(ItemStack stack, ItemDisplayContext displayContext,
                                                    boolean leftHanded, PoseStack poseStack,
                                                    MultiBufferSource multiBufferSource, int light, int overlay,
                                                    BakedModel model, CallbackInfo ci,
                                                    @Local(ordinal = 2) LocalBooleanRef useDirectConsumer,
                                                    @Share("isBlockItem") LocalBooleanRef isBlockItemRef) {
        // Use faster cached check for translucency instead of multiple instanceof checks
        if (ItemRendererStates.ITEM_FRAME != null && stack.getItem() instanceof BlockItem blockItem) {
            isBlockItemRef.set(true);
            useDirectConsumer.set(!((BakedOpacity) model).moreculling$hasTextureTranslucency(
                    blockItem.getBlock().defaultBlockState()
            ));
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(" +
                            "Lnet/minecraft/world/item/ItemDisplayContext;)" +
                            "Lnet/minecraft/client/renderer/block/model/ItemTransform;"
            )
    )
    private ItemTransform moreculling$getTransformation(
            ItemTransforms modelTransformation, ItemDisplayContext renderMode,
            Operation<ItemTransform> original, @Share("transformation") LocalRef<ItemTransform> transformationRef
    ) {
        ItemTransform transformation = original.call(modelTransformation, renderMode);
        if (ItemRendererStates.ITEM_FRAME != null) {
            transformationRef.set(transformation);
        }
        return transformation;
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
                                         @Share("transformation") LocalRef<ItemTransform> transformationRef,
                                         @Share("isBlockItem") LocalBooleanRef isBlockItemRef) {
        ItemFrame frame = ItemRendererStates.ITEM_FRAME;
        if (frame == null) {
            ItemRendererStates.DIRECTIONS = null;
            return;
        }
        Vec3 cameraPos = ItemRendererStates.CAMERA.getPosition();
        Vec3 framePos = frame.position();
        boolean isBlockItem = isBlockItemRef.get();
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

    @Redirect(
            method = "renderModelLists",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
            )
    )
    private Direction[] moreculling$modifyDirections() {
        return ItemRendererStates.DIRECTIONS == null ? DirectionUtils.DIRECTIONS : ItemRendererStates.DIRECTIONS;
    }

    @WrapOperation(
            method = "renderModelLists",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(" +
                            "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"
            )
    )
    private List<BakedQuad> moreculling$onlySomeFaces$Vanilla(BakedModel instance, BlockState blockState,
                                                              Direction direction, RandomSource random,
                                                              Operation<List<BakedQuad>> original) {
        if (ItemRendererStates.DIRECTIONS != null) {
            List<BakedQuad> bakedQuads = new ArrayList<>(original.call(instance, blockState, direction, random));
            Iterator<BakedQuad> iterator = bakedQuads.iterator();
            quads:
            while (iterator.hasNext()) {
                BakedQuad bakedQuad = iterator.next();
                Direction face = bakedQuad.getDirection();
                for (Direction dir : ItemRendererStates.DIRECTIONS) {
                    if (face == dir) {
                        continue quads;
                    }
                }
                iterator.remove();
            }
            return bakedQuads;
        }
        return original.call(instance, blockState, direction, random);
    }
}
