package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.map.MapOpacity;
import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderer;
import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import ca.fxco.moreculling.utils.CullingUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.fxco.moreculling.utils.CullingUtils.shouldShowMapFace;

@Mixin(value = ItemFrameRenderer.class, priority = 1200)
public abstract class ItemFrameRenderer_cullMixin<T extends ItemFrame> extends EntityRenderer<T, ItemFrameRenderState> {

    @Unique
    private static final Direction[] MAP_RENDER_SIDES = new Direction[]{
            Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST
    };

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Shadow @Final private MapRenderer mapRenderer;

    @Shadow protected abstract int getLightCoords(boolean bl, int i, int j);

    protected ItemFrameRenderer_cullMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                    "Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(" +
                            "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                            "Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void moreculling$optimizedRender(ItemFrameRenderState itemFrameState, PoseStack poseStack,
                                             SubmitNodeCollector submitNodeCollector, CameraRenderState renderState,
                                             CallbackInfo ci) {
        if (!MoreCulling.CONFIG.useCustomItemFrameRenderer) {
            return;
        }
        ci.cancel();
        poseStack.pushPose();
        Direction direction = itemFrameState.direction;
        Vec3 vec3d = this.getRenderOffset(itemFrameState);
        poseStack.translate(-vec3d.x(), -vec3d.y(), -vec3d.z());
        double d = 0.46875;
        poseStack.translate(
                (double)direction.getStepX() * d,
                (double)direction.getStepY() * d,
                (double)direction.getStepZ() * d
        );
        float xRot;
        float yRot;
        if (direction.getAxis().isHorizontal()) {
            xRot = 0.0F;
            yRot = 180.0F - direction.toYRot();
        } else {
            xRot = (float)(-90 * direction.getAxisDirection().getStep());
            yRot = 180.0F;
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        ItemStackRenderState itemStackState = itemFrameState.item;
        boolean skipFrontRender = false;
        if (!itemStackState.isEmpty()) {
            poseStack.pushPose();
            MapId mapIdComponent = itemFrameState.mapId;
            if (mapIdComponent != null) {
                MapItemSavedData mapState = MapItem.getSavedData(mapIdComponent, Minecraft.getInstance().level);
                if (mapState != null) { // Map is present
                    if (shouldShowMapFace(direction, itemFrameState,
                            this.entityRenderDispatcher.camera.getPosition())) {
                        skipFrontRender = !((MapOpacity) mapState).moreculling$hasTransparency();
                        double di;
                        double offsetZFighting = itemFrameState.isInvisible ? 0.5 :
                                skipFrontRender ?
                                        ((di = this.entityRenderDispatcher.camera.getPosition().distanceToSqr(itemFrameState.x, itemFrameState.y - 1, itemFrameState.z) / 5000) > 6 ?
                                                Math.max(0.4452 - di, 0.4) : 0.4452) :
                                        0.4375;
                        poseStack.translate(0.0, 0.0, offsetZFighting);
                        int j = itemFrameState.rotation % 4 * 2;
                        poseStack.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0f / 8.0f));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                        float h = 0.0078125f;
                        poseStack.scale(h, h, h);
                        poseStack.translate(-64.0, -64.0, 0.0);
                        poseStack.translate(0.0, 0.0, -1.0);
                        mapRenderer.render(
                                itemFrameState.mapRenderState,
                                poseStack,
                                submitNodeCollector,
                                true,
                                this.getLightCoords(
                                        itemFrameState.isGlowFrame,
                                        15728880,
                                        itemFrameState.lightCoords
                                )
                        );
                    }
                }
            } else if (!itemStackState.isEmpty()) {
                poseStack.translate(0.0, 0.0, itemFrameState.isInvisible ? 0.5 : 0.4375);
                poseStack.mulPose(Axis.ZP.rotationDegrees(
                        (float) itemFrameState.rotation * 360.0f / 8.0f)
                );
                int l = this.getLightCoords(itemFrameState.isGlowFrame, LightTexture.FULL_BRIGHT, itemFrameState.lightCoords);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                // Use extended item renderer here
                ((ExtendedItemStackRenderState) itemStackState).moreculling$renderItemFrameItem(
                        poseStack,
                        submitNodeCollector,
                        l,
                        itemFrameState,
                        this.entityRenderDispatcher.camera
                );
            }
            poseStack.popPose();
        }
        if (!itemFrameState.isInvisible) { // Render Item Frame block model
            BlockState blockstate = BlockStateDefinitions.getItemFrameFakeState(itemFrameState.isGlowFrame, itemFrameState.mapId != null);
            BlockStateModel blockstatemodel = this.blockRenderer.getBlockModel(blockstate);
            poseStack.translate(-0.5, -0.5, -0.5);
            var modelRenderer = (ExtendedBlockModelRenderer) this.blockRenderer.getModelRenderer();
            if (CullingUtils.shouldCullBack(itemFrameState)) {
                if (skipFrontRender) {
                    submitNodeCollector.submitCustomGeometry(poseStack,
                            RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                            (pose, consumer) -> {
                                modelRenderer.moreculling$renderModelForFaces(
                                        pose,
                                        consumer,
                                        null,
                                        blockstatemodel,
                                        1.0f,
                                        1.0f,
                                        1.0f,
                                        1.0f,
                                        itemFrameState.lightCoords,
                                        OverlayTexture.NO_OVERLAY,
                                        MAP_RENDER_SIDES
                                );
                            }
                    );
                } else {
                    submitNodeCollector.submitCustomGeometry(poseStack,
                            RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                            (pose, consumer) -> {
                                modelRenderer.moreculling$renderModelWithoutFace(
                                        pose,
                                        consumer,
                                        null,
                                        blockstatemodel,
                                        1.0f,
                                        1.0f,
                                        1.0f,
                                        1.0f,
                                        itemFrameState.lightCoords,
                                        OverlayTexture.NO_OVERLAY,
                                        Direction.SOUTH
                                );
                            }
                    );
                }
            } else {
                if (skipFrontRender) {
                    submitNodeCollector.submitCustomGeometry(poseStack,
                            RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                            (pose, consumer) -> {
                                modelRenderer.moreculling$renderModelWithoutFace(
                                        pose,
                                        consumer,
                                        null,
                                        blockstatemodel,
                                        1.0f,
                                        1.0f,
                                        1.0f,
                                        1.0f,
                                        itemFrameState.lightCoords,
                                        OverlayTexture.NO_OVERLAY,
                                        Direction.NORTH
                                );
                            }
                    );
                } else {
                    submitNodeCollector.submitBlockModel(
                            poseStack,
                            RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                            blockstatemodel,
                            1.0f,
                            1.0f,
                            1.0f,
                            itemFrameState.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            itemFrameState.outlineColor
                    );
                }
            }
        }
        poseStack.popPose();
    }
}
