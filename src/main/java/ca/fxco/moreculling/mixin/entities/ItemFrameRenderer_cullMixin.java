package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.map.MapOpacity;
import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderer;
import ca.fxco.moreculling.api.renderers.ExtendedItemRenderer;
import ca.fxco.moreculling.utils.CullingUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
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
public abstract class ItemFrameRenderer_cullMixin<T extends ItemFrame> extends EntityRenderer<T> {

    @Unique
    private static final Direction[] MAP_RENDER_SIDES = new Direction[]{
            Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST
    };

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Shadow
    protected abstract ModelResourceLocation getFrameModelResourceLoc(T entity, ItemStack stack);

    @Shadow
    protected abstract int getLightVal(T itemFrame, int glowLight, int regularLight);

    @Shadow public abstract Vec3 getRenderOffset(T itemFrame, float f);

    protected ItemFrameRenderer_cullMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    @Inject(
            method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;" +
                    "FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(" +
                            "Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void moreculling$optimizedRender(T itemFrameEntity, float f, float g, PoseStack poseStack,
                                             MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.useCustomItemFrameRenderer) {
            return;
        }
        ci.cancel();
        poseStack.pushPose();
        Direction direction = itemFrameEntity.getDirection();
        Vec3 vec3d = this.getRenderOffset(itemFrameEntity, g);
        poseStack.translate(-vec3d.x(), -vec3d.y(), -vec3d.z());
        double d = 0.46875;
        poseStack.translate(
                (double) direction.getStepX() * d,
                (double) direction.getStepY() * d,
                (double) direction.getStepZ() * d
        );
        poseStack.mulPose(Axis.XP.rotationDegrees(itemFrameEntity.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - itemFrameEntity.getYRot()));
        boolean isInvisible = itemFrameEntity.isInvisible();
        ItemStack itemStack = itemFrameEntity.getItem();
        boolean skipFrontRender = false;
        if (!itemStack.isEmpty()) {
            poseStack.pushPose();
            MapId mapIdComponent = itemFrameEntity.getFramedMapId();
            if (mapIdComponent != null) {
                MapItemSavedData mapState = MapItem.getSavedData(mapIdComponent, itemFrameEntity.level());
                if (mapState != null) { // Map is present
                    if (shouldShowMapFace(direction, itemFrameEntity.position(),
                            this.entityRenderDispatcher.camera.getPosition())) {
                        skipFrontRender = !((MapOpacity) mapState).moreculling$hasTransparency();
                        double di;
                        double offsetZFighting = isInvisible ? 0.5 :
                                skipFrontRender ?
                                        ((di = this.entityRenderDispatcher.distanceToSqr(itemFrameEntity) / 5000) > 6 ?
                                                Math.max(0.4452 - di, 0.4) : 0.4452) :
                                        0.4375;
                        poseStack.translate(0.0, 0.0, offsetZFighting);
                        int j = itemFrameEntity.getRotation() % 4 * 2;
                        poseStack.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0f / 8.0f));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                        float h = 0.0078125f;
                        poseStack.scale(h, h, h);
                        poseStack.translate(-64.0, -64.0, 0.0);
                        poseStack.translate(0.0, 0.0, -1.0);
                        Minecraft.getInstance().gameRenderer.getMapRenderer().render(
                                poseStack,
                                multiBufferSource,
                                mapIdComponent,
                                mapState,
                                true,
                                this.getLightVal(
                                        itemFrameEntity,
                                        LightTexture.FULL_SKY | 0xD2,
                                        i
                                )
                        );
                    }
                }
            } else {
                poseStack.translate(0.0, 0.0, isInvisible ? 0.5 : 0.4375);
                poseStack.mulPose(Axis.ZP.rotationDegrees(
                        (float) itemFrameEntity.getRotation() * 360.0f / 8.0f)
                );
                int l = this.getLightVal(itemFrameEntity, LightTexture.FULL_BRIGHT, i);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                // Use extended item renderer here
                ((ExtendedItemRenderer) this.itemRenderer).moreculling$renderItemFrameItem(
                        itemStack,
                        poseStack,
                        multiBufferSource,
                        l,
                        itemFrameEntity,
                        this.entityRenderDispatcher.camera
                );
            }
            poseStack.popPose();
        }
        if (!isInvisible) { // Render Item Frame block model
            ModelManager modelManager = this.blockRenderer.getBlockModelShaper().getModelManager();
            ModelResourceLocation modelResourceLocation = this.getFrameModelResourceLoc(itemFrameEntity, itemStack);
            poseStack.translate(-0.5, -0.5, -0.5);
            var modelRenderer = (ExtendedBlockModelRenderer) this.blockRenderer.getModelRenderer();
            if (CullingUtils.shouldCullBack(itemFrameEntity)) {
                if (skipFrontRender) {
                    modelRenderer.moreculling$renderModelForFaces(
                            poseStack.last(),
                            multiBufferSource.getBuffer(Sheets.solidBlockSheet()),
                            null,
                            modelManager.getModel(modelResourceLocation),
                            1.0f,
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.NO_OVERLAY,
                            MAP_RENDER_SIDES
                    );
                } else {
                    modelRenderer.moreculling$renderModelWithoutFace(
                            poseStack.last(),
                            multiBufferSource.getBuffer(Sheets.solidBlockSheet()),
                            null,
                            modelManager.getModel(modelResourceLocation),
                            1.0f,
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.NO_OVERLAY,
                            Direction.SOUTH
                    );
                }
            } else {
                if (skipFrontRender) {
                    modelRenderer.moreculling$renderModelWithoutFace(
                            poseStack.last(),
                            multiBufferSource.getBuffer(Sheets.solidBlockSheet()),
                            null,
                            modelManager.getModel(modelResourceLocation),
                            1.0f,
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.NO_OVERLAY,
                            Direction.NORTH
                    );
                } else {
                    this.blockRenderer.getModelRenderer().renderModel(
                            poseStack.last(),
                            multiBufferSource.getBuffer(Sheets.solidBlockSheet()),
                            null,
                            modelManager.getModel(modelResourceLocation),
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.NO_OVERLAY
                    );
                }
            }
        }
        poseStack.popPose();
    }
}
