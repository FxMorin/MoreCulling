package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedQuad.class)
public class BakedQuad_cacheMixin implements QuadOpacity {

    @Shadow
    @Final
    protected TextureAtlasSprite sprite;

    @Unique
    private Boolean moreculling$hasTranslucency;

    @Override
    public boolean moreculling$getTextureTranslucency() {
        return moreculling$hasTranslucency == null ?
                moreculling$hasTranslucency = ((SpriteOpacity) sprite).moreculling$hasTranslucency() :
                moreculling$hasTranslucency;
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        moreculling$hasTranslucency = null;
    }
}