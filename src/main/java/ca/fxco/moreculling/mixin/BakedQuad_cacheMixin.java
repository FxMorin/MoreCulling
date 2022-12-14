package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedQuad.class)
public class BakedQuad_cacheMixin implements QuadOpacity {

    @Shadow
    @Final
    protected Sprite sprite;

    @Unique
    private Boolean hasTranslucency;

    @Override
    public boolean getTextureTranslucency() {
        return hasTranslucency == null ? hasTranslucency = ((SpriteOpacity)sprite).hasTranslucency() : hasTranslucency;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = null;
    }
}