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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ca.fxco.moreculling.utils.DirectionUtils.shiftDirection;
import static net.minecraft.util.math.Direction.NORTH;
import static net.minecraft.util.math.Direction.SOUTH;

@Mixin(value = ItemRenderer.class, priority = 1100)
public class ItemRenderer_faceCullingMixin {

    @Redirect(
            method = "renderItem(Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/client/render/model/json/ModelTransformationMode;" +
                    "ZLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;" +
                    "IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/json/ModelTransformationMode;isFirstPerson()Z",
                    ordinal = 0
            )
    )
    private boolean moreculling$skipSlowTransparencyChecks(ModelTransformationMode renderMode) {
        return ItemRendererStates.ITEM_FRAME != null;
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/client/render/model/json/ModelTransformationMode;" +
                    "ZLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;" +
                    "IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayers;getItemLayer(" +
                            "Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private void moreculling$fastTransparencyChecks(ItemStack stack, ModelTransformationMode renderMode,
                                                    boolean leftHanded, MatrixStack matrices,
                                                    VertexConsumerProvider vertexConsumers, int light, int overlay,
                                                    BakedModel model, CallbackInfo ci,
                                                    @Local(ordinal = 2) LocalBooleanRef useDirectConsumer,
                                                    @Share("isBlockItem") LocalBooleanRef isBlockItemRef) {
        // Use faster cached check for translucency instead of multiple instanceof checks
        if (ItemRendererStates.ITEM_FRAME != null && stack.getItem() instanceof BlockItem blockItem) {
            isBlockItemRef.set(true);
            useDirectConsumer.set(!((BakedOpacity) model).moreculling$hasTextureTranslucency(
                    blockItem.getBlock().getDefaultState()
            ));
        }
    }

    @WrapOperation(
            method = "renderItem(Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/client/render/model/json/ModelTransformationMode;" +
                    "ZLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;" +
                    "IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/json/ModelTransformation;getTransformation(" +
                            "Lnet/minecraft/client/render/model/json/ModelTransformationMode;)" +
                            "Lnet/minecraft/client/render/model/json/Transformation;"
            )
    )
    private Transformation moreculling$getTransformation(
            ModelTransformation modelTransformation, ModelTransformationMode renderMode,
            Operation<Transformation> original, @Share("transformation") LocalRef<Transformation> transformationRef
    ) {
        Transformation transformation = original.call(modelTransformation, renderMode);
        if (ItemRendererStates.ITEM_FRAME != null) {
            transformationRef.set(transformation);
        }
        return transformation;
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/client/render/model/json/ModelTransformationMode;" +
                    "ZLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;" +
                    "IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(" +
                            "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;" +
                            "IILnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumer;)V"
            )
    )
    private void moreculling$faceRemoval(ItemStack stack, ModelTransformationMode renderMode,
                                         boolean leftHanded, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, int overlay,
                                         BakedModel model, CallbackInfo ci,
                                         @Share("transformation") LocalRef<Transformation> transformationRef,
                                         @Share("isBlockItem") LocalBooleanRef isBlockItemRef) {
        ItemFrameEntity frame = ItemRendererStates.ITEM_FRAME;
        if (frame == null) {
            ItemRendererStates.DIRECTIONS = null;
            return;
        }
        Vec3d cameraPos = ItemRendererStates.CAMERA.getPos();
        Vec3d framePos = frame.getPos();
        boolean isBlockItem = isBlockItemRef.get();
        Transformation transformation = transformationRef.get();
        boolean canCull = ((!isBlockItem && !frame.isInvisible()) || CullingUtils.shouldCullBack(frame)) &&
                TransformationUtils.canCullTransformation(transformation);
        double dist = ItemRendererStates.CAMERA.getPos().distanceTo(framePos);
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
                Direction facing = frame.getHorizontalFacing();
                Direction dirX = shiftDirection(facing,
                        cameraPos.x > framePos.x ? Direction.EAST : Direction.WEST, rotation);
                Direction dirY = shiftDirection(facing,
                        cameraPos.y > framePos.y ? Direction.UP : Direction.DOWN, rotation);
                Direction dirZ = shiftDirection(facing,
                        cameraPos.z > framePos.z ? Direction.SOUTH : Direction.NORTH, rotation);
                ItemRendererStates.DIRECTIONS = new Direction[] { dirX, dirY, dirZ };
            } else {
                if (canCull) {
                    ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(
                            DirectionUtils.changeDirectionUsingTransformation(Direction.NORTH, transformation)
                    );
                } else {
                    ItemRendererStates.DIRECTIONS = null;
                }
            }
        }
    }

    @Redirect(
            method = "renderBakedItemModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    private Direction[] moreculling$modifyDirections() {
        return ItemRendererStates.DIRECTIONS == null ? DirectionUtils.DIRECTIONS : ItemRendererStates.DIRECTIONS;
    }

    @WrapOperation(
            method = "renderBakedItemModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/BakedModel;getQuads(" +
                            "Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;" +
                            "Lnet/minecraft/util/math/random/Random;)Ljava/util/List;"
            )
    )
    private List<BakedQuad> moreculling$onlySomeFaces$Vanilla(BakedModel instance, BlockState blockState,
                                                              Direction direction, Random random,
                                                              Operation<List<BakedQuad>> original) {
        if (ItemRendererStates.DIRECTIONS != null) {
            List<BakedQuad> bakedQuads = new ArrayList<>(original.call(instance, blockState, direction, random));
            Iterator<BakedQuad> iterator = bakedQuads.iterator();
            quads:
            while (iterator.hasNext()) {
                BakedQuad bakedQuad = iterator.next();
                Direction face = bakedQuad.getFace();
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
