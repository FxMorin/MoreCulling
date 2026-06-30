package ca.fxco.moreculling.api.renderers;

import ca.fxco.moreculling.api.renderers.modelsubmit.MorecullingBlockModelSubmit;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class MorecullingFabricBlockModelFeatureRenderer extends MorecullingBlockModelFeatureRenderer {
    private final QuadInstance quadInstance = new QuadInstance();
    private final BufferCache bufferCache = new BufferCache();
    private final MutableQuadViewImpl emitter = new MutableQuadViewImpl() {
        {
            data = new int[EncodingFormat.TOTAL_STRIDE];
            clear();
        }

        @Override
        protected void emitDirectly() {
            if (!submit.shouldCull(this.cullFace() != null ? this.cullFace() : this.nominalFace())) {
                bufferQuad(this);
            }
        }
    };

    private MorecullingBlockModelSubmit submit;

    @Override
    protected void buildGroup(@NonNull FeatureFrameContext context, List<MorecullingBlockModelSubmit> submits) {
        BufferCache bufferCache = this.bufferCache;
        MutableQuadViewImpl emitter = this.emitter;

        for (MorecullingBlockModelSubmit submit : submits) {
            bufferCache.prepare(submit.renderType(), submit.sheetedDecalPose());

            quadInstance.setLightCoords(submit.lightCoords());
            quadInstance.setOverlayCoords(submit.overlayCoords());

            for (BlockStateModelPart part : submit.modelParts()) {
                putPartQuads(part, submit.pose(), quadInstance, submit.tintColor(), submit.tintLayers(), bufferCache);
            }

            if (submit.mesh() != null) {
                this.submit = submit;
                ((Mesh) submit.mesh()).outputTo(emitter);
            }
        }

        bufferCache.clear();
        submit = null;
    }

    private void putPartQuads(BlockStateModelPart part, PoseStack.Pose pose, QuadInstance quadInstance, int baseTintColor, int[] tintLayers, BufferCache bufferCache) {
        for (Direction direction : DirectionUtils.DIRECTIONS) {
            for (BakedQuad quad : part.getQuads(direction)) {
                VertexConsumer buffer = bufferCache.getBuffer(quad.materialInfo().layer());

                if (buffer == null) {
                    continue;
                }

                putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
            }
        }

        for (BakedQuad quad : part.getQuads(null)) {
            VertexConsumer buffer = bufferCache.getBuffer(quad.materialInfo().layer());

            if (buffer == null) {
                continue;
            }

            putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
        }
    }

    private void bufferQuad(MutableQuadViewImpl quad) {
        VertexConsumer buffer = bufferCache.getBuffer(quad.chunkLayer());

        if (buffer == null) {
            return;
        }

        if (quad.emissive()) {
            quad.lightmap(LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT);
        } else {
            quad.minLightmap(submit.lightCoords());
        }

        int[] tintLayers = submit.tintLayers();
        int baseTintColor = submit.tintColor();

        int tintIndex = quad.tintIndex();
        boolean useTintLayer = tintIndex != -1 && tintIndex < tintLayers.length;
        quad.multiplyColor(useTintLayer ? ARGB.multiply(baseTintColor, tintLayers[tintIndex]) : baseTintColor);
        quad.buffer(submit.overlayCoords(), submit.pose(), buffer);
    }

    private static void putQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, int baseTintColor, int[] tintLayers, VertexConsumer buffer) {
        int tintIndex = quad.materialInfo().tintIndex();
        boolean useTintLayer = tintIndex != -1 && tintIndex < tintLayers.length;
        instance.setColor(useTintLayer ? ARGB.multiply(baseTintColor, tintLayers[tintIndex]) : baseTintColor);
        buffer.putBakedQuad(pose, quad, instance);
    }

    public class BufferCache {
        private RenderType renderTypeFunction;
        private PoseStack.@Nullable Pose sheetedDecalPose;

        @Nullable
        private ChunkSectionLayer lastLayer;
        @Nullable
        private VertexConsumer lastBuffer;

        public void prepare(RenderType renderTypeFunction, PoseStack.@Nullable Pose sheetedDecalPose) {
            this.renderTypeFunction = renderTypeFunction;
            this.sheetedDecalPose = sheetedDecalPose;
            lastLayer = null;
        }

        public void clear() {
            renderTypeFunction = null;
            sheetedDecalPose = null;
            lastLayer = null;
            lastBuffer = null;
        }

        @Nullable
        public VertexConsumer getBuffer(ChunkSectionLayer layer) {
            if (layer != lastLayer) {
                lastLayer = layer;
                RenderType renderType = renderTypeFunction;

                if (renderType == null) {
                    lastBuffer = null;
                } else {
                    VertexConsumer buffer = getVertexBuilder(renderType);
                    lastBuffer = sheetedDecalPose != null ? new SheetedDecalTextureGenerator(buffer, sheetedDecalPose, 1.0F) : buffer;
                }
            }

            return lastBuffer;
        }
    }
}
