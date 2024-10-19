package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * This interface allows you to call the MoreCulling methods used for item rendering.
 * This gives you access to the custom MoreCulling rendering methods, in order to allow everyone to benefit from the
 * performance boost that MoreCulling can offer.<br/>
 * Use these methods over ones provided in {@link ItemRenderer}
 *
 * @since 0.8.0
 */

public interface ExtendedItemRenderer {

    /**
     * This will render a baked item model without a specific face. (Item Frame)
     * This can be used to skip the face of an item that cannot be seen
     *
     * @since 0.25.0
     */
    void moreculling$renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                     PoseStack poseStack, VertexConsumer vertices,
                                                     @Nullable Direction withoutFace);

    /**
     * This will render a single face of a baked item model. (Item Frame)
     * This is used for LOD rendering at far distances.
     *
     * @since 0.25.0
     */
    void moreculling$renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                 PoseStack poseStack, VertexConsumer vertices, Direction face);

    /**
     * This will render 3 sides of a baked item model. (Item Frame)
     * This is part of the 3-face rendering technique.
     *
     * @since 0.25.0
     */
    void moreculling$renderBakedItemModelOnly3Faces(BakedModel model, ItemStack stack, int light, int overlay,
                                                    PoseStack poseStack, VertexConsumer vertices,
                                                    Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render an item as if it was in an item frame like MoreCulling, it will automatically include all of
     * MoreCulling's optimizations.
     *
     * @since 0.25.0
     */
    void moreculling$renderItemFrameItem(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                         int light, ItemFrameRenderState frame, Camera camera);
}