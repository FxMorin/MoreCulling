package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.utils.VertexUtils;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedQuad.class)
public class BakedQuad_cacheMixin implements QuadOpacity {

    @Shadow
    @Final
    private BakedQuad.MaterialInfo materialInfo;
    @Unique
    private Boolean moreculling$hasTranslucency;

    @Override
    public boolean moreculling$getTextureTranslucency() {
        if (moreculling$hasTranslucency != null) {
            return moreculling$hasTranslucency;
        }

        SpriteOpacity opacity = ((SpriteOpacity) materialInfo.sprite());
        NativeImage image = opacity.moreculling$getUnmipmappedImage();
        QuadBounds bounds = VertexUtils.getQuadUvBounds((BakedQuad) (Object) this, image.getWidth(), image.getHeight());
        return moreculling$hasTranslucency = opacity.moreculling$hasTranslucency(bounds);
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        moreculling$hasTranslucency = null;
    }
}