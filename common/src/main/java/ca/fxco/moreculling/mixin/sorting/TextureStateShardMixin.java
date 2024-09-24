package ca.fxco.moreculling.mixin.sorting;

import ca.fxco.moreculling.states.SortingStates;
import ca.fxco.moreculling.utils.AbstractTextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderStateShard.TextureStateShard.class)
public class TextureStateShardMixin {

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void moreculling$getLatestTexture(ResourceLocation pTexture, boolean pBlur, boolean pMipmap,
                                              CallbackInfo ci) {
        // Texture manager is null during startup.
        // None of the dynamic textures we are targeting are called during that stage.
        // This also means we can easily skip most non-dynamic render types very easily during game launch.
        try { // TODO: Fix exceptions
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            if (texturemanager != null &&
                    !AbstractTextureUtils.doesHaveTranslucency(pTexture, texturemanager.getTexture(pTexture))) {
                SortingStates.CAN_SKIP_SORTING = true;
            }
        } catch (Exception ignore) {}
    }
}
