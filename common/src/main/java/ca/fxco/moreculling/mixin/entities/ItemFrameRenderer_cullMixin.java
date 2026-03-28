package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderState;
import ca.fxco.moreculling.api.renderers.ExtendedMapRenderState;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.CullingUtils;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemFrameRenderer.class, priority = 1200)
public abstract class ItemFrameRenderer_cullMixin<T extends ItemFrame> extends EntityRenderer<T, ItemFrameRenderState> {

    @Unique
    private static final Direction[] MAP_RENDER_SIDES = new Direction[]{
            Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST
    };

    @Shadow @Final private MapRenderer mapRenderer;

    @Shadow protected abstract int getLightCoords(boolean bl, int i, int j);

    protected ItemFrameRenderer_cullMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                    "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockModelRenderState;" +
                            "submitWithZOffset(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void moreculling$cullFrame(BlockModelRenderState instance, PoseStack poseStack,
                                     SubmitNodeCollector submitNodeCollector, int lightCoords,
                                     int overlayCoords, int outlineColor, Operation<Void> original,
                                     @Local(argsOnly = true) ItemFrameRenderState state) {
        if (!MoreCulling.CONFIG.useCustomItemFrameRenderer) {
            original.call(instance, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
            return;
        }

        boolean skipFrontRender = !((ExtendedMapRenderState) state.mapRenderState).moreculling$hasTransparency();
        if (CullingUtils.shouldCullBack(state)) {
            if (skipFrontRender) {
                ((ExtendedBlockModelRenderState) state.frameModel).moreculling$submitModelForFaces(
                        RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                        poseStack,
                        submitNodeCollector,
                        lightCoords,
                        overlayCoords,
                        outlineColor,
                        MAP_RENDER_SIDES
                );
            } else {
                ((ExtendedBlockModelRenderState) state.frameModel).moreculling$submitModelWithoutFace(
                        RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                        poseStack,
                        submitNodeCollector,
                        lightCoords,
                        overlayCoords,
                        state.outlineColor,
                        Direction.SOUTH
                );
            }
        } else {
            if (skipFrontRender) {
                ((ExtendedBlockModelRenderState) state.frameModel).moreculling$submitModelWithoutFace(
                        RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                        poseStack,
                        submitNodeCollector,
                        lightCoords,
                        overlayCoords,
                        outlineColor,
                        Direction.NORTH
                );
            } else {
                original.call(instance, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
            }
        }
    }

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                    "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "submit(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void moreculling$cullItem(ItemStackRenderState instance, PoseStack poseStack,
                                      SubmitNodeCollector submitNodeCollector,
                                      int lightCoords, int overlayCoords, int outlineColor,
                                      Operation<Void> original,
                                      @Local(argsOnly = true) ItemFrameRenderState state) {
        if (!MoreCulling.CONFIG.useCustomItemFrameRenderer) {
            original.call(instance, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
            return;
        }

        ItemRendererStates.ITEM_FRAME = state;
        ItemRendererStates.CAMERA = this.entityRenderDispatcher.camera;
        original.call(instance, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
        ItemRendererStates.ITEM_FRAME = null;
        ItemRendererStates.CAMERA = null;
    }


    @Definition(
            id = "mapId",
            field = "Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;mapId:" +
                    "Lnet/minecraft/world/level/saveddata/maps/MapId;"
    )
    @Definition(id = "state", local = @Local(type = ItemFrameRenderState.class, argsOnly = true))
    @Expression("state.mapId != null")
    @ModifyExpressionValue(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                    "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION"
            )
    )
    private boolean moreculling$cullMap(boolean original, @Local(argsOnly = true) ItemFrameRenderState state,
                                        @Local Direction direction, @Local(argsOnly = true) PoseStack poseStack) {
        if (!original) {
            return false;
        }

        if (CullingUtils.shouldShowMapFace(direction, state,
                this.entityRenderDispatcher.camera.position())) {

            if (!state.isInvisible && !((ExtendedMapRenderState) state.mapRenderState).moreculling$hasTransparency()) {
                    double di = this.entityRenderDispatcher.camera.position()
                            .distanceToSqr(state.x, state.y - 1, state.z) / 5000;
                    poseStack.translate(0.0, 0.0, (di > 6 ? Math.max(0.4452 - di, 0.4) : 0.4452) - 0.4375);
            }
                            ;
            return true;
        }
        return false;
    }
}
