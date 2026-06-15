package ca.fxco.moreculling.api.renderers;

import ca.fxco.moreculling.api.renderers.modelsubmit.MorecullingBlockModelSubmit;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;

import java.util.List;

public class MorecullingBlockModelFeatureRenderer extends RenderTypeFeatureRenderer<MorecullingBlockModelSubmit> {
    public static final FeatureRendererType<MorecullingBlockModelSubmit> TYPE = FeatureRendererType.create("Moreculling Block Model");
    private final QuadInstance quadInstance = new QuadInstance();

    @Override
    protected void buildGroup(FeatureFrameContext context, List<MorecullingBlockModelSubmit> submits) {
        for (MorecullingBlockModelSubmit submit : submits) {
            VertexConsumer buffer = this.getVertexBuilder(submit.renderType());
            VertexConsumer wrappedBuffer = submit.sheetedDecalPose() != null
                    ? new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0F)
                    : buffer;
            this.quadInstance.setLightCoords(submit.lightCoords());
            this.quadInstance.setOverlayCoords(submit.overlayCoords());

            for (BlockStateModelPart part : submit.modelParts()) {
                putPartQuads(submit, part, submit.pose(), this.quadInstance, submit.tintColor(), submit.tintLayers(), wrappedBuffer);
            }
        }
    }

    private static void putPartQuads(
            MorecullingBlockModelSubmit submit,
            BlockStateModelPart part, PoseStack.Pose pose, QuadInstance quadInstance,
            int baseTintColor, int[] tintLayers, VertexConsumer buffer
    ) {
        for (Direction direction : DirectionUtils.DIRECTIONS) {
            if (submit.shouldCull(direction)) {
                continue;
            }
            for (BakedQuad quad : part.getQuads(direction)) {
                putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
            }
        }

        for (BakedQuad quad : part.getQuads(null)) {
            if (submit.shouldCull(quad.direction())) {
                continue;
            }
            putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
        }
    }

    private static void putQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, int baseTintColor, int[] tintLayers, VertexConsumer buffer) {
        int tintIndex = quad.materialInfo().tintIndex();
        boolean useTintLayer = tintIndex != -1 && tintIndex < tintLayers.length;
        instance.setColor(useTintLayer ? ARGB.multiply(baseTintColor, tintLayers[tintIndex]) : baseTintColor);
        buffer.putBakedQuad(pose, quad, instance);
    }
}
