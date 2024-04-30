package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemRenderer;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class ItemRenderer_apiMixin implements ExtendedItemRenderer {

    @Shadow protected abstract void renderModelLists(BakedModel model, ItemStack stack, int light,
                                                     int overlay, PoseStack poseStack, VertexConsumer vertices);

    @Shadow public abstract void renderStatic(ItemStack stack, ItemDisplayContext itemDisplayContext, int light,
                                              int overlay, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                              @Nullable Level level, int seed);

    @Override
    public void moreculling$renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                            PoseStack pose, VertexConsumer vertices,
                                                            @Nullable Direction withoutFace) {
        ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(withoutFace);
        this.renderModelLists(model, stack, light, overlay, pose, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderBakedItemModelOnly3Faces(BakedModel model, ItemStack stack, int light, int overlay,
                                                           PoseStack pose, VertexConsumer vertices,
                                                           Direction faceX, Direction faceY, Direction faceZ) {
        ItemRendererStates.DIRECTIONS = new Direction[] { faceX, faceY, faceZ };
        this.renderModelLists(model, stack, light, overlay, pose, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                        PoseStack pose, VertexConsumer vertices,
                                                        Direction face) {
        ItemRendererStates.DIRECTIONS = new Direction[] { face };
        this.renderModelLists(model, stack, light, overlay, pose, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderItemFrameItem(ItemStack stack, PoseStack pose,
                                                MultiBufferSource multiBufferSource,
                                                int light, ItemFrame frame, Camera camera) {
        ItemRendererStates.ITEM_FRAME = frame;
        ItemRendererStates.CAMERA = camera;
        this.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose,
                multiBufferSource, frame.level(), frame.getId());
        ItemRendererStates.ITEM_FRAME = null;
        ItemRendererStates.CAMERA = null;
    }
}
