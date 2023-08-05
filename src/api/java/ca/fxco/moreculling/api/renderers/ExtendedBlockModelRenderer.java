package ca.fxco.moreculling.api.renderers;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface allows you to call the MoreCulling methods used for block model rendering.
 * This gives you access to the custom MoreCulling rendering methods, in order to allow everyone to benefit from the
 * performance boost that MoreCulling can offer.<br/>
 * Use these methods over ones provided in {@link net.minecraft.client.render.block.BlockModelRenderer}
 *
 * @since 0.9.0
 */

public interface ExtendedBlockModelRenderer {

    /**
     * This will render a single quad. In vanilla this is mostly done all at once, although we call each quad to be
     * rendered separately, this is done since we don't render all quads like vanilla
     *
     * @since 0.9.0
     */
    void renderQuad(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green, float blue,
                    BakedQuad bakedQuad, int light, int overlay);

    /**
     * This will render a model without a specific face.
     * This can be used to skip the face of a block model that cannot be seen
     *
     * @since 0.9.0
     */
    void renderModelWithoutFace(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                                BakedModel bakedModel, float red, float green, float blue, int light, int overlay,
                                Direction withoutFace);

    /**
     * This will render quads without a specific face.
     * This can be used to skip the face of quads that cannot be seen
     *
     * @since 0.9.0
     */
    void renderQuadsWithoutFace(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green,
                                float blue, List<BakedQuad> quads, int light, int overlay, Direction withoutFace);

    /**
     * This will render a single face of a model.
     *
     * @since 0.9.0
     */
    void renderModelForFace(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                            BakedModel bakedModel, float red, float green, float blue, int light, int overlay,
                            Direction forFace);

    /**
     * This will render a single face of quads.
     *
     * @since 0.9.0
     */
    void renderQuadsForFace(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green,
                            float blue, List<BakedQuad> quads, int light, int overlay, Direction forFace);

    /**
     * This will render 3 sides of the model.
     * This is part of the 3-face rendering technique.
     *
     * @since 0.9.0
     */
    void renderModelFor3Faces(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                              BakedModel bakedModel, float red, float green, float blue, int light, int overlay,
                              Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render 3 sides of quads.
     * This is part of the 3-face rendering technique.
     *
     * @since 0.9.0
     */
    void renderQuadsFor3Faces(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green,
                              float blue, List<BakedQuad> quads, int light, int overlay,
                              Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render a list of faces for a model.
     *
     * @since 0.9.0
     */
    void renderModelForFaces(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                             BakedModel bakedModel, float red, float green, float blue, int light, int overlay,
                             Direction[] faces);

    /**
     * This will render all quads for a list of faces.
     *
     * @since 0.9.0
     */
    void renderQuadsForFaces(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green,
                             float blue, List<BakedQuad> quads, int light, int overlay,
                             Direction[] faces);
}
