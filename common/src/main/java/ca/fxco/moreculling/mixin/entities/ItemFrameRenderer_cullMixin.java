package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.map.MapOpacity;
import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderState;
import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import ca.fxco.moreculling.utils.CullingUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.MapItem;
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

    @Shadow @Final private MapRenderer mapRenderer;

    @Shadow protected abstract int getLightCoords(boolean bl, int i, int j);

    protected ItemFrameRenderer_cullMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                    "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(" +
                            "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                            "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void moreculling$optimizedRender(ItemFrameRenderState state, PoseStack poseStack,
                                             SubmitNodeCollector submitNodeCollector, CameraRenderState renderState,
                                             CallbackInfo ci) {
        if (!MoreCulling.CONFIG.useCustomItemFrameRenderer) {
            return;
        }
        ci.cancel();
        poseStack.pushPose();
        Direction direction = state.direction;
        Vec3 renderOffset = this.getRenderOffset(state);
        poseStack.translate(-renderOffset.x(), -renderOffset.y(), -renderOffset.z());
        double offs = 0.46875;
        poseStack.translate(
                direction.getStepX() * offs,
                direction.getStepY() * offs,
                direction.getStepZ() * offs
        );
        float xRot;
        float yRot;
        if (direction.getAxis().isHorizontal()) {
            xRot = 0.0F;
            yRot = 180.0F - direction.toYRot();
        } else {
            xRot = -90 * direction.getAxisDirection().getStep();
            yRot = 180.0F;
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.pushPose();
        boolean skipFrontRender = false;
        MapId mapIdComponent = state.mapId;
        if (mapIdComponent != null) {
            MapItemSavedData mapState = MapItem.getSavedData(mapIdComponent, Minecraft.getInstance().level);
            if (mapState != null) { // Map is present
                if (shouldShowMapFace(direction, state,
                        this.entityRenderDispatcher.camera.position())) {
                    skipFrontRender = !((MapOpacity) mapState).moreculling$hasTransparency();
                    double di;
                    double offsetZFighting = state.isInvisible ? 0.5 :
                            skipFrontRender ?
                                    ((di = this.entityRenderDispatcher.camera.position().distanceToSqr(state.x, state.y - 1, state.z) / 5000) > 6 ?
                                            Math.max(0.4452 - di, 0.4) : 0.4452) :
                                    0.4375;
                    poseStack.translate(0.0, 0.0, offsetZFighting);
                    int rotation = state.rotation % 4 * 2;
                    poseStack.mulPose(Axis.ZP.rotationDegrees(rotation * 360.0f / 8.0f));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                    float s = 0.0078125f;
                    poseStack.scale(s, s, s);
                    poseStack.translate(-64.0, -64.0, 0.0);
                    poseStack.translate(0.0, 0.0, -1.0);
                    mapRenderer.render(
                            state.mapRenderState,
                            poseStack,
                            submitNodeCollector,
                            true,
                            this.getLightCoords(
                                    state.isGlowFrame,
                                    15728880,
                                    state.lightCoords
                            )
                    );
                }
            }
        } else if (!state.item.isEmpty()) {
            poseStack.translate(0.0, 0.0, state.isInvisible ? 0.5 : 0.4375);
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotation * 360.0F / 8.0F));
            int lightVal = this.getLightCoords(state.isGlowFrame, 15728880, state.lightCoords);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            ((ExtendedItemStackRenderState) state.item).moreculling$submitItemFrameItem(
                    poseStack,
                    submitNodeCollector,
                    lightVal,
                    state,
                    this.entityRenderDispatcher.camera
            );
        }
        poseStack.popPose();

        if (!state.frameModel.isEmpty()) {
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            if (CullingUtils.shouldCullBack(state)) {
                if (skipFrontRender) {
                    ((ExtendedBlockModelRenderState) state.frameModel).moreculling$submitModelForFaces(
                            RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                            poseStack,
                            submitNodeCollector,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor,
                            MAP_RENDER_SIDES
                    );
                } else {
                    ((ExtendedBlockModelRenderState) state.frameModel).moreculling$submitModelWithoutFace(
                            RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS),
                            poseStack,
                            submitNodeCollector,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
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
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor,
                            Direction.NORTH
                    );
                } else {
                    state.frameModel.submitWithZOffset(
                            poseStack,
                            submitNodeCollector,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor
                    );
                }
            }
        }
        poseStack.popPose();
    }
}
