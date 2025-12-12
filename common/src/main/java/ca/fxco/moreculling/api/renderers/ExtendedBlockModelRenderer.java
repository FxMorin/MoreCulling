package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface allows you to call the MoreCulling methods used for block model rendering.
 * This gives you access to the custom MoreCulling rendering methods, in order to allow everyone to benefit from the
 * performance boost that MoreCulling can offer.<br/>
 * Use these methods over ones provided in {@link ModelBlockRenderer}
 *
 * @since 0.9.0
 */

public interface ExtendedBlockModelRenderer {

    /**
     * This will render a single quad. In vanilla this is mostly done all at once, although we call each quad to be
     * rendered separately, this is done since we don't render all quads like vanilla
     *
     * @since 0.25.0
     */
    void moreculling$renderQuad(PoseStack.Pose pose, VertexConsumer vertices, float red, float green, float blue,
                                float alpha, BakedQuad bakedQuad, int light, int overlay);

    /**
     * This will render a model without a specific face.
     * This can be used to skip the face of a block model that cannot be seen
     *
     * @since 0.25.0
     */
    void moreculling$renderModelWithoutFace(PoseStack.Pose pose, VertexConsumer vertices,
                                            @Nullable BlockState state, BlockStateModel bakedModel, float red,
                                            float green, float blue, float alpha, int light, int overlay,
                                            Direction withoutFace);

    /**
     * This will render quads without a specific face.
     * This can be used to skip the face of quads that cannot be seen
     *
     * @since 0.25.0
     */
    void moreculling$renderQuadsWithoutFace(PoseStack.Pose pose, VertexConsumer vertices, float red, float green,
                                            float blue, float alpha, List<BakedQuad> quads, int light, int overlay,
                                            Direction withoutFace);

    /**
     * This will render a single face of a model.
     *
     * @since 0.25.0
     */
    void moreculling$renderModelForFace(PoseStack.Pose pose, VertexConsumer vertices, @Nullable BlockState state,
                                        BlockStateModel bakedModel, float red, float green, float blue, float alpha,
                                        int light, int overlay, Direction forFace);

    /**
     * This will render a single face of quads.
     *
     * @since 0.25.0
     */
    void moreculling$renderQuadsForFace(PoseStack.Pose pose, VertexConsumer vertices, float red, float green,
                                        float blue, float alpha, List<BakedQuad> quads, int light, int overlay,
                                        Direction forFace);

    /**
     * This will render 3 sides of the model.
     * This is part of the 3-face rendering technique.
     *
     * @since 0.25.0
     */
    void moreculling$renderModelFor3Faces(PoseStack.Pose pose, VertexConsumer vertices, @Nullable BlockState state,
                                          BlockStateModel bakedModel, float red, float green, float blue, float alpha,
                                          int light, int overlay, Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render 3 sides of quads.
     * This is part of the 3-face rendering technique.
     *
     * @since 0.25.0
     */
    void moreculling$renderQuadsFor3Faces(PoseStack.Pose pose, VertexConsumer vertices, float red, float green,
                                          float blue, float alpha, List<BakedQuad> quads, int light, int overlay,
                                          Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render a list of faces for a model.
     *
     * @since 0.25.0
     */
    void moreculling$renderModelForFaces(PoseStack.Pose pose, VertexConsumer vertices, @Nullable BlockState state,
                                         BlockStateModel bakedModel, float red, float green, float blue, float alpha,
                                         int light, int overlay, Direction[] faces);

    /**
     * This will render all quads for a list of faces.
     *
     * @since 0.25.0
     */
    void moreculling$renderQuadsForFaces(PoseStack.Pose post, VertexConsumer vertices, float red, float green,
                                         float blue, float alpha, List<BakedQuad> quads, int light, int overlay,
                                         Direction[] faces);
}
