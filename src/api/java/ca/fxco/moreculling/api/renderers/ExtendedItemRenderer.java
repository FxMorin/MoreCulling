package ca.fxco.moreculling.api.renderers;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface allows you to call the MoreCulling methods used for item rendering.
 * Unlike other interfaces provided by MoreCulling, this one is less configurable.
 * It was made public to allow others to modify how the item renderer is used more easily and to allow others to use
 * the faster rendering methods created by MoreCulling
 */

public interface ExtendedItemRenderer {

    /**
     * This will return the baked quad color cache used for item rendering by MoreCulling.
     * Should only be used for item frame baked quad colors!
     * The size of this map is 256, since it is very unlikely that there are more than 256 custom quad colors used in
     * a single frame.
     * @return Baked Quad Color Cache
     * @since 0.8.0
     */
    ThreadLocal<Object2IntLinkedOpenHashMap<BakedQuad>> getBakedQuadColorCache();

    /**
     * Works the same as {@link net.minecraft.client.render.item.ItemRenderer#getModel} except it skips all the mojank
     * checks for tridents and spyglass since they are not require for item frame rendering and just a
     * waste of performance.
     * @return BakedModel of item stack
     * @since 0.8.0
     */
    BakedModel customGetModel(ItemStack stack, int seed);

    /**
     * This will render a single quad. In vanilla this is mostly done all at once, although we call each quad to be
     * rendered separately, this is done since we don't render all quads like vanilla. Also this method is drastically
     * faster than the vanilla quad renderer, since we use quad color caching. (Item Frame)
     * @since 0.8.0
     */
    void renderBakedItemQuad(VertexConsumer vertices, ItemStack stack, int light, int overlay,
                             MatrixStack.Entry entry, BakedQuad bakedQuad);

    /**
     * This will render a baked items quads without a specific face. (Item Frame)
     * This can be used to skip the face of an item that cannot be seen
     * @since 0.8.0
     */
    void renderBakedItemQuadsWithoutFace(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                         ItemStack stack, int light, int overlay, Direction withoutFace);

    /**
     * This will render a baked item model without a specific face. (Item Frame)
     * This can be used to skip the face of an item that cannot be seen
     * @since 0.8.0
     */
    void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                         MatrixStack matrices, VertexConsumer vertices,
                                         @Nullable Direction withoutFace);

    /**
     * This will render a single face of a baked items quads. (Item Frame)
     * This is used for LOD rendering at far distances.
     * @since 0.8.0
     */
    void renderBakedItemQuadsForFace(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                     ItemStack stack, int light, int overlay, Direction face);

    /**
     * This will render a single face of a baked item model. (Item Frame)
     * This is used for LOD rendering at far distances.
     * @since 0.8.0
     */
    void renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices,
                                     VertexConsumer vertices, Direction face);

    /**
     * This will render 3 sides of a baked items quads. (Item Frame)
     * This is part of the 3-face rendering technique.
     * @since 0.8.0
     */
    void renderBakedItemQuadsFor3Faces(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                       ItemStack stack, int light, int overlay,
                                       Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render 3 sides of a baked item model. (Item Frame)
     * This is part of the 3-face rendering technique.
     * @since 0.8.0
     */
    void renderBakedItemModelOnly3Faces(BakedModel model, ItemStack stack, int light, int overlay,
                                        MatrixStack matrices, VertexConsumer vertices,
                                        Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render an item in an item frame like MoreCulling, it will automatically include all of MoreCulling's
     * optimizations.
     * @since 0.8.0
     */
    void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vc, int light, int seed,
                             ItemFrameEntity frame, Vec3d cameraPos);
}