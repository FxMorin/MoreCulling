package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemRenderer;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class ItemRenderer_apiMixin implements ExtendedItemRenderer {

    @Shadow protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light,
                                                         int overlay, MatrixStack matrices, VertexConsumer vertices);

    @Shadow public abstract void renderItem(ItemStack stack, ModelTransformationMode transformationType, int light,
                                            int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                            @Nullable World world, int seed);

    @Override
    public void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                MatrixStack matrices, VertexConsumer vertices,
                                                @Nullable Direction withoutFace) {
        ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(withoutFace);
        this.renderBakedItemModel(model, stack, light, overlay, matrices, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void renderBakedItemModelOnly3Faces(BakedModel model, ItemStack stack, int light, int overlay,
                                               MatrixStack matrices, VertexConsumer vertices,
                                               Direction faceX, Direction faceY, Direction faceZ) {
        ItemRendererStates.DIRECTIONS = new Direction[] { faceX, faceY, faceZ };
        this.renderBakedItemModel(model, stack, light, overlay, matrices, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay,
                                            MatrixStack matrices, VertexConsumer vertices,
                                            Direction face) {
        ItemRendererStates.DIRECTIONS = new Direction[] { face };
        this.renderBakedItemModel(model, stack, light, overlay, matrices, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                    int light, ItemFrameEntity frame, Camera camera) {
        ItemRendererStates.ITEM_FRAME = frame;
        ItemRendererStates.CAMERA = camera;
        this.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices,
                vertexConsumers, frame.getWorld(), frame.getId());
        ItemRendererStates.ITEM_FRAME = null;
        ItemRendererStates.CAMERA = null;
    }
}
