package ca.fxco.moreculling.api.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

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
     * This will submit a model without a specific face.
     * This can be used to skip the face of a block model that cannot be seen
     *
     * @since 1.7.0
     */
    void moreculling$submitModelWithoutFace(RenderType renderType, PoseStack poseStack,
                                            SubmitNodeCollector submitNodeCollector, int lightCoords,
                                            int overlayCoords, int outlineColor, Direction withoutFace);

    /**
     * This will submit a single face of a model.
     *
     * @since 1.7.0
     */
    void moreculling$submitModelForFace(RenderType renderType, PoseStack poseStack,
                                            SubmitNodeCollector submitNodeCollector, int lightCoords,
                                            int overlayCoords, int outlineColor, Direction forFace);

    /**
     * This will submit 3 sides of the model.
     * This is part of the 3-face rendering technique.
     *
     * @since 1.7.0
     */
    void moreculling$submitModelFor3Faces(RenderType renderType, PoseStack poseStack,
                                          SubmitNodeCollector submitNodeCollector, int lightCoords,
                                          int overlayCoords, int outlineColor, Direction faceX, Direction faceY, Direction faceZ);


    /**
     * This will submit a list of faces for a model.
     *
     * @since 1.7.0
     */
    void moreculling$submitModelForFaces(RenderType renderType, PoseStack poseStack,
                                            SubmitNodeCollector submitNodeCollector, int lightCoords,
                                            int overlayCoords, int outlineColor, Direction[] faces);
}
