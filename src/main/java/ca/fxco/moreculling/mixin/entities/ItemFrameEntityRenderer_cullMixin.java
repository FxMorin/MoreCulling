package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.map.MapOpacity;
import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderer;
import ca.fxco.moreculling.api.renderers.ExtendedItemRenderer;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

import static ca.fxco.moreculling.utils.CullingUtils.shouldShowMapFace;

@Mixin(value = ItemFrameEntityRenderer.class, priority = 1200)
public abstract class ItemFrameEntityRenderer_cullMixin<T extends ItemFrameEntity> extends EntityRenderer<T> {

    @Unique
    private static final Direction[] MAP_RENDER_SIDES = new Direction[] {
            Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST
    };

    @Shadow
    @Final
    private BlockRenderManager blockRenderManager;

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Shadow
    protected abstract ModelIdentifier getModelId(T entity, ItemStack stack);

    @Shadow
    protected abstract int getLight(T itemFrame, int glowLight, int regularLight);

    protected ItemFrameEntityRenderer_cullMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/ItemFrameEntity;" +
                    "FFLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;" +
                            "render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void optimizedRender(T itemFrameEntity, float f, float g, MatrixStack matrixStack,
                                VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.useCustomItemFrameRenderer) return;
        ci.cancel();
        matrixStack.push();
        Direction direction = itemFrameEntity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate(
                (double)direction.getOffsetX() * d,
                (double)direction.getOffsetY() * d,
                (double)direction.getOffsetZ() * d
        );
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(itemFrameEntity.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - itemFrameEntity.getYaw()));
        boolean isInvisible = itemFrameEntity.isInvisible();
        ItemStack itemStack = itemFrameEntity.getHeldItemStack();
        boolean skipFrontRender = false;
        if (!itemStack.isEmpty()) {
            matrixStack.push();
            OptionalInt optionalInt = itemFrameEntity.getMapId();
            if (optionalInt.isPresent()) {
                int mapId = optionalInt.getAsInt();
                MapState mapState = FilledMapItem.getMapState(mapId, itemFrameEntity.world);
                if (mapState != null) { // Map is present
                    if (shouldShowMapFace(direction, itemFrameEntity.getPos(), this.dispatcher.camera.getPos())) {
                        skipFrontRender = !((MapOpacity) mapState).hasTransparency();
                        double di;
                        double offsetZFighting = isInvisible ? 0.5 :
                                skipFrontRender ?
                                        ((di = this.dispatcher.getSquaredDistanceToCamera(itemFrameEntity) / 6000) > 6 ?
                                                0.4452 - di : 0.4452) :
                                        0.4375;
                        matrixStack.translate(0.0, 0.0, offsetZFighting);
                        int j = itemFrameEntity.getRotation() % 4 * 2;
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) j * 360.0f / 8.0f));
                        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                        float h = 0.0078125f;
                        matrixStack.scale(h, h, h);
                        matrixStack.translate(-64.0, -64.0, 0.0);
                        matrixStack.translate(0.0, 0.0, -1.0);
                        MinecraftClient.getInstance().gameRenderer.getMapRenderer().draw(
                                matrixStack,
                                vertexConsumerProvider,
                                mapId,
                                mapState,
                                true,
                                this.getLight(
                                        itemFrameEntity,
                                        LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE | 0xD2,
                                        i
                                )
                        );
                    }
                }
            } else {
                matrixStack.translate(0.0, 0.0, isInvisible ? 0.5 : 0.4375);
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(
                        (float)itemFrameEntity.getRotation() * 360.0f / 8.0f)
                );
                int l = this.getLight(itemFrameEntity, LightmapTextureManager.MAX_LIGHT_COORDINATE, i);
                matrixStack.scale(0.5f, 0.5f, 0.5f);
                // Use extended item renderer here
                ((ExtendedItemRenderer) this.itemRenderer).renderItemFrameItem(
                        itemStack,
                        matrixStack,
                        vertexConsumerProvider,
                        l,
                        itemFrameEntity.getId(),
                        itemFrameEntity,
                        this.dispatcher.camera.getPos()
                );
            }
            matrixStack.pop();
        }
        if (!isInvisible) { // Render Item Frame block model
            BakedModelManager bakedModelManager = this.blockRenderManager.getModels().getModelManager();
            ModelIdentifier modelIdentifier = this.getModelId(itemFrameEntity, itemStack);
            matrixStack.translate(-0.5, -0.5, -0.5);
            if (CullingUtils.shouldCullBack(itemFrameEntity)) {
                if (skipFrontRender) {
                    ((ExtendedBlockModelRenderer) this.blockRenderManager.getModelRenderer()).renderModelForFaces(
                            matrixStack.peek(),
                            vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()),
                            null,
                            bakedModelManager.getModel(modelIdentifier),
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.DEFAULT_UV,
                            MAP_RENDER_SIDES
                    );
                } else {
                    ((ExtendedBlockModelRenderer) this.blockRenderManager.getModelRenderer()).renderModelWithoutFace(
                            matrixStack.peek(),
                            vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()),
                            null,
                            bakedModelManager.getModel(modelIdentifier),
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.DEFAULT_UV,
                            Direction.SOUTH
                    );
                }
            } else {
                if (skipFrontRender) {
                    ((ExtendedBlockModelRenderer) this.blockRenderManager.getModelRenderer()).renderModelWithoutFace(
                            matrixStack.peek(),
                            vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()),
                            null,
                            bakedModelManager.getModel(modelIdentifier),
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.DEFAULT_UV,
                            Direction.NORTH
                    );
                } else {
                    this.blockRenderManager.getModelRenderer().render(
                            matrixStack.peek(),
                            vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()),
                            null,
                            bakedModelManager.getModel(modelIdentifier),
                            1.0f,
                            1.0f,
                            1.0f,
                            i,
                            OverlayTexture.DEFAULT_UV
                    );
                }
            }
        }
        matrixStack.pop();
    }
}
