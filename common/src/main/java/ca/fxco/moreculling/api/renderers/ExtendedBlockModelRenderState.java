package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;

import java.util.List;

/**
 * This interface allows you to call the MoreCulling methods used for block model rendering.
 * This gives you access to the custom MoreCulling rendering methods, in order to allow everyone to benefit from the
 * performance boost that MoreCulling can offer.<br/>
 * Use these methods over ones provided in {@link ModelBlockRenderer}
 *
 * @since 0.9.0
 */

public interface ExtendedBlockModelRenderState {

    /**
     * This will render a single quad. In vanilla this is mostly done all at once, although we call each quad to be
     * rendered separately, this is done since we don't render all quads like vanilla
     *
     * @since 1.7.0
     */
    void moreculling$renderQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance,
                                int[] tintLayers, VertexConsumer buffer);

    /**
     * This will render a model without a specific face.
     * This can be used to skip the face of a block model that cannot be seen
     *
     * @since 1.7.0
     */
    void moreculling$renderModelWithoutFace(PoseStack.Pose pose, VertexConsumer vertices,
                                            List<BlockStateModelPart> bakedModel, int[] tintLayers,  int light,
                                            int overlay,
                                            Direction withoutFace);
    /**
     * This will submit a model without a specific face.
     * This can be used to skip the face of a block model that cannot be seen
     *
     * @since 1.7.0
     */
    void moreculling$submitModelWithoutFace(RenderType renderType, PoseStack poseStack,
                                            SubmitNodeCollector submitNodeCollector, int lightCoords,
                                            int overlayCoords, int outlineColor, Direction withoutFace);

    /**
     * This will render quads without a specific face.
     * This can be used to skip the face of quads that cannot be seen
     *
     * @since 1.7.0
     */
    void moreculling$renderQuadsWithoutFace(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                            List<BakedQuad> quads, QuadInstance instance,
                                            Direction withoutFace);

    /**
     * This will render a single face of a model.
     *
     * @since 1.7.0
     */
    void moreculling$renderModelForFace(PoseStack.Pose pose, VertexConsumer vertices,
                                        List<BlockStateModelPart> model, int[] tintLayers,
                                        int light, int overlay, Direction forFace);

    /**
     * This will render a single face of quads.
     *
     * @since 1.7.0
     */
    void moreculling$renderQuadsForFace(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                        List<BakedQuad> quads, QuadInstance instance,
                                        Direction forFace);

    /**
     * This will render 3 sides of the model.
     * This is part of the 3-face rendering technique.
     *
     * @since 1.7.0
     */
    void moreculling$renderModelFor3Faces(PoseStack.Pose pose, VertexConsumer vertices,
                                          List<BlockStateModelPart> model, int[] tintLayers,
                                          int light, int overlay, Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render 3 sides of quads.
     * This is part of the 3-face rendering technique.
     *
     * @since 1.7.0
     */
    void moreculling$renderQuadsFor3Faces(PoseStack.Pose pose, VertexConsumer vertices, int[] tintLayers,
                                          List<BakedQuad> quads, QuadInstance instance,
                                          Direction faceX, Direction faceY, Direction faceZ);

    /**
     * This will render a list of faces for a model.
     *
     * @since 1.7.0
     */
    void moreculling$renderModelForFaces(PoseStack.Pose pose, VertexConsumer vertices,
                                         List<BlockStateModelPart> model, int[] tintLayers,
                                         int light, int overlay, Direction[] faces);


    /**
     * This will submit a list of faces for a model.
     *
     * @since 1.7.0
     */
    void moreculling$submitModelForFaces(RenderType renderType, PoseStack poseStack,
                                            SubmitNodeCollector submitNodeCollector, int lightCoords,
                                            int overlayCoords, int outlineColor, Direction[] faces);

    /**
     * This will render all quads for a list of faces.
     *
     * @since 1.7.0
     */
    void moreculling$renderQuadsForFaces(PoseStack.Pose post, VertexConsumer vertices, int[] tintLayers,
                                         List<BakedQuad> quads, QuadInstance instance,
                                         Direction[] faces);
}
