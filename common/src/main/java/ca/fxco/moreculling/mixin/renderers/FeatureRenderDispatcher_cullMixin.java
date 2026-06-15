package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.MorecullingBlockModelFeatureRenderer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(conflict = @Condition("fabric-renderer-api-v1"))
@Mixin(FeatureRenderDispatcher.class)
public abstract class FeatureRenderDispatcher_cullMixin {

    @Shadow
    @Final
    private FeatureRendererMap featureRenderers;

    @Inject(
            method = "<init>",
            at = @At(value = "NEW", target = "()Lnet/minecraft/client/renderer/feature/MovingBlockFeatureRenderer;")
    )
    private void moreculling$registerMorecullingModelSubmits(RenderBuffers renderBuffers, ModelManager modelManager, AtlasManager atlasManager, Font font, GameRenderState gameRenderState, CallbackInfo ci) {
        this.featureRenderers.put(MorecullingBlockModelFeatureRenderer.TYPE, new MorecullingBlockModelFeatureRenderer());
    }
}
