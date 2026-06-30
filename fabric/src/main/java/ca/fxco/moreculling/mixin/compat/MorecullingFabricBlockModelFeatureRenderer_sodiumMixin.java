package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.api.renderers.MorecullingFabricBlockModelFeatureRenderer;
import ca.fxco.moreculling.api.renderers.modelsubmit.MorecullingBlockModelSubmit;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.caffeinemc.mods.sodium.client.render.frapi.wrapper.ExtendedMutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.model.EncodingFormat;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = {@Condition("sodium"), @Condition("fabric-renderer-api-v1")})
@Mixin(MorecullingFabricBlockModelFeatureRenderer.class)
public class MorecullingFabricBlockModelFeatureRenderer_sodiumMixin {
    @Shadow private MorecullingBlockModelSubmit submit;
    @Final
    @Shadow private MorecullingFabricBlockModelFeatureRenderer.BufferCache bufferCache;
    @Unique
    private final MutableQuadViewImpl emitter = new MutableQuadViewImpl() {
        {
            data = new int[EncodingFormat.TOTAL_STRIDE];
            clear();
        }

        @Override
        public void emitDirectly() {
            if (!submit.shouldCull(this.getCullFace() != null ? this.getCullFace() : this.getNominalFace())) {
                bufferQuad(this);
            }
        }
    };

    @WrapOperation(
            method = "buildGroup",
            at = @At(value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/Mesh;" +
                            "outputTo(Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;)V"
            )
    )
    private void render(Mesh instance, QuadEmitter emitter, Operation<Void> original) {
        original.call(instance, ((ExtendedMutableQuadViewImpl) this.emitter).getWrapper());
    }

    private void bufferQuad(MutableQuadViewImpl quad) {
        VertexConsumer buffer = bufferCache.getBuffer(quad.getRenderType());

        if (buffer == null) {
            return;
        }

        if (quad.emissive()) {
            ((ExtendedMutableQuadViewImpl) quad).getWrapper().lightmap(LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT);
        } else {
            ((ExtendedMutableQuadViewImpl) quad).getWrapper().minLightmap(submit.lightCoords());
        }

        int[] tintLayers = submit.tintLayers();
        int baseTintColor = submit.tintColor();

        int tintIndex = quad.getTintIndex();
        boolean useTintLayer = tintIndex != -1 && tintIndex < tintLayers.length;
        ((ExtendedMutableQuadViewImpl) quad).getWrapper().multiplyColor(useTintLayer ? ARGB.multiply(baseTintColor, tintLayers[tintIndex]) : baseTintColor);
        ((ExtendedMutableQuadViewImpl) quad).getWrapper().buffer(submit.overlayCoords(), submit.pose(), buffer);
    }
}
