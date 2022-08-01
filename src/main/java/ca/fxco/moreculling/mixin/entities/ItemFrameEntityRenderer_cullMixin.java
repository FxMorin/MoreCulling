package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderer;
import ca.fxco.moreculling.api.renderers.ExtendedItemRenderer;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

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

    // TODO: Clean up this code

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
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(itemFrameEntity.getPitch()));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - itemFrameEntity.getYaw()));
        boolean bl = itemFrameEntity.isInvisible();
        ItemStack itemStack = itemFrameEntity.getHeldItemStack();
        boolean renderingMap = false;
        if (!itemStack.isEmpty()) {
            matrixStack.push();
            OptionalInt optionalInt = itemFrameEntity.getMapId();
            matrixStack.translate(0.0, 0.0, bl ? 0.5 : 0.4452); // 0.4375 - actually made it flat lmao
            int j = optionalInt.isPresent() ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)j * 360.0f / 8.0f));
            if (optionalInt.isPresent()) {
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                float h = 0.0078125f;
                matrixStack.scale(h, h, h);
                matrixStack.translate(-64.0, -64.0, 0.0);
                MapState mapState = FilledMapItem.getMapState(optionalInt.getAsInt(), itemFrameEntity.world);
                matrixStack.translate(0.0, 0.0, -1.0);
                if (mapState != null) {
                    int k = this.getLight(
                            itemFrameEntity,
                            LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE | 0xD2,
                            i
                    );
                    MinecraftClient.getInstance().gameRenderer.getMapRenderer().draw(
                            matrixStack,
                            vertexConsumerProvider,
                            optionalInt.getAsInt(),
                            mapState,
                            true,
                            k
                    );
                    renderingMap = true;
                }
            } else {
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
        if (!bl) {
            BakedModelManager bakedModelManager = this.blockRenderManager.getModels().getModelManager();
            ModelIdentifier modelIdentifier = this.getModelId(itemFrameEntity, itemStack);
            matrixStack.push();
            matrixStack.translate(-0.5, -0.5, -0.5);
            if (CullingUtils.shouldCullBack(itemFrameEntity)) {
                if (renderingMap) {
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
                if (renderingMap) {
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
            matrixStack.pop();
        }
        matrixStack.pop();
    }
}
