package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.CulledBlockModelConsumer;
import ca.fxco.moreculling.api.renderers.modelsubmit.MorecullingBlockModelSubmit;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.renderer.BlockModelBufferCache;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.BlockFeatureRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition("fabric-renderer-api-v1"))
@Mixin(BlockFeatureRenderer.class)
public abstract class BlockFeatureRenderer_fabricCullMixin {
    @Shadow
    @Final
    private QuadInstance quadInstance;

    @Shadow
    private static void putQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance,
                                int[] tintLayers, VertexConsumer buffer, @Nullable VertexConsumer outlineBuffer) {
    }

    @Inject(
            method = "renderBlockModelSubmits",
            at = @At("RETURN")
    )
    private void moreculling$renderMorecullingModelSubmits(SubmitNodeCollection nodeCollection,
                                                           MultiBufferSource.BufferSource bufferSource,
                                                           OutlineBufferSource outlineBufferSource,
                                                           boolean translucent, CallbackInfo ci) {
        BlockModelBufferCache bufferCache = new BlockModelBufferCache(bufferSource, outlineBufferSource);
        CulledBlockModelConsumer quadConsumer = new CulledBlockModelConsumer();
        QuadEmitter output = Renderer.get().quadEmitter(quadConsumer);

        for (MorecullingBlockModelSubmit submit : nodeCollection.moreculling$getBlockModelSubmits()) {
            if (submit.renderType().hasBlending() == translucent) {

                bufferCache.outlineColor(submit.outlineColor());

                this.quadInstance.setLightCoords(submit.lightCoords());
                this.quadInstance.setOverlayCoords(submit.overlayCoords());

                for (BlockStateModelPart part : submit.modelParts()) {
                    moreculling$putPartQuads(submit, part, submit.pose(), this.quadInstance, submit.tintLayers(), bufferCache.getBuffer(submit.renderType()), bufferCache.getOutlineBuffer(submit.renderType()));
                }

                if (submit.mesh() != null) {
                    quadConsumer.tintLayers = submit.tintLayers();
                    quadConsumer.lightCoords = submit.lightCoords();
                    quadConsumer.overlayCoords = submit.overlayCoords();
                    quadConsumer.pose = submit.pose();
                    quadConsumer.renderTypeFunction = _ -> submit.renderType();
                    quadConsumer.bufferCache = bufferCache;
                    quadConsumer.submit = submit;
                    ((Mesh) submit.mesh()).outputTo(output);
                }
            }
        }
    }

    @Unique
    private static void moreculling$putPartQuads(
            MorecullingBlockModelSubmit submit,
            BlockStateModelPart part,
            PoseStack.Pose pose,
            QuadInstance quadInstance,
            int[] tintLayers,
            VertexConsumer buffer,
            VertexConsumer outlineBuffer
    ) {
        for (Direction direction : DirectionUtils.DIRECTIONS) {
            if (submit.shouldCull(direction)) {
                continue;
            }
            for (BakedQuad quad : part.getQuads(direction)) {
                putQuad(pose, quad, quadInstance, tintLayers, buffer, outlineBuffer);
            }
        }

        for (BakedQuad quad : part.getQuads(null)) {
            if (submit.shouldCull(quad.direction())) {
                continue;
            }
            putQuad(pose, quad, quadInstance, tintLayers, buffer, outlineBuffer);
        }
    }
}
