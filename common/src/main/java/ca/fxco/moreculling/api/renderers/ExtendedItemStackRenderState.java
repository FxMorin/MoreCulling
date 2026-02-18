package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface allows you to call the MoreCulling methods used for item rendering.
 * This gives you access to the custom MoreCulling rendering methods, in order to allow everyone to benefit from the
 * performance boost that MoreCulling can offer.<br/>
 * Use these methods over ones provided in {@link ItemStackRenderState}
 *
 * @since 0.8.0
 */

public interface ExtendedItemStackRenderState {

    /**
     * This will render a baked item model without a specific face. (Item Frame)
     * This can be used to skip the face of an item that cannot be seen
     *
     * @since 1.6.3
     */
    void moreculling$submitBakedItemModelWithoutFace(PoseStack poseStack, SubmitNodeCollector nodeCollector,
                                                     int packedLight, int packedOverlay, int outlineColor,
                                                     @Nullable Direction withoutFace);

    /**
     * This will render a single face of a baked item model. (Item Frame)
     * This is used for LOD rendering at far distances.
     *
     * @since 1.6.3
     */
    void moreculling$submitBakedItemModelForFace(PoseStack poseStack, SubmitNodeCollector nodeCollector,
                                                 int packedLight, int packedOverlay, int outlineColor, Direction face);

    /**
     * This will render 3 sides of a baked item model. (Item Frame)
     * This is part of the 3-face rendering technique.
     *
     * @since 1.6.3
     */
    void moreculling$submitBakedItemModelOnly3Faces(PoseStack poseStack, SubmitNodeCollector nodeCollector,
                                                    int packedLight, int packedOverlay, int outlineColor,
                                                    Direction faceX, Direction faceY, Direction faceZ);


    /**
     * This will render an item as if it was in an item frame like MoreCulling, it will automatically include all of
     * MoreCulling's optimizations.
     *
     * @since 1.6.3
     */
    void moreculling$submitItemFrameItem(PoseStack poseStack, SubmitNodeCollector multiBufferSource,
                                         int light, ItemFrameRenderState frame, Camera camera);
}