package ca.fxco.moreculling.api.renderers;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * This interface allows you to call the MoreCulling methods used for item rendering.
 * This gives you access to the custom MoreCulling rendering methods, in order to allow everyone to benefit from the
 * performance boost that MoreCulling can offer.<br/>
 * Use these methods over ones provided in {@link net.minecraft.client.render.item.ItemRenderer}
 *
 * @since 0.8.0
 */

public interface ExtendedItemRenderer {

    /**
     * This will return the baked quad color cache used for item rendering by MoreCulling.
     * Should only be used for item frame baked quad colors!
     * The size of this map is 256, since it is very unlikely that there are more than 256 custom quad colors used in
     * a single frame.
     *
     * @return Baked Quad Color Cache
     * @since 0.8.0
     */
    ThreadLocal<Object2IntLinkedOpenHashMap<BakedQuad>> getBakedQuadColorCache();

    /**
     * This will render a baked item model without a specific face. (Item Frame)
     * This can be used to skip the face of an item that cannot be seen
     *
     * @since 0.8.0
     */
    void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                         MatrixStack matrices, VertexConsumer vertices,
                                         @Nullable Direction withoutFace);

    /**
     * This will render a single face of a baked item model. (Item Frame)
     * This is used for LOD rendering at far distances.
     *
     * @since 0.8.0
     */
    void renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices,
                                     VertexConsumer vertices, Direction face);

    /**
     * This will render 3 sides of a baked item model. (Item Frame)
     * This is part of the 3-face rendering technique.
     *
     * @since 0.8.0
     */
    void renderBakedItemModelOnly3Faces(BakedModel model, ItemStack stack, int light, int overlay,
                                        MatrixStack matrices, VertexConsumer vertices,
                                        Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render an item as if it was in an item frame like MoreCulling, it will automatically include all of
     * MoreCulling's optimizations.
     *
     * @since 0.20.0
     */
    void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                             ItemFrameEntity frame, Camera camera);
}