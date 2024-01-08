package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.mixin.accessors.SpriteContentsAccessor;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Sprite.class)
public class Sprite_opacityMixin implements SpriteOpacity {

    @Shadow
    @Final
    private SpriteContents contents;

    @Override
    public NativeImage getUnmipmappedImage() {
        return ((SpriteContentsAccessor) contents).getImage();
    }

    @Override
    public boolean hasTransparency() {
        return SpriteUtils.doesHaveTransparency(getUnmipmappedImage());
    }

    @Override
    public boolean hasTransparency(QuadBounds bounds) {
        return SpriteUtils.doesHaveTransparency(getUnmipmappedImage());
    }

    @Override
    public boolean hasTranslucency() {
        return hasTranslucency((List<NativeImage>)null);
    }

    @Override
    public boolean hasTranslucency(QuadBounds bounds) {
        return hasTranslucency(bounds, null);
    }

    @Override
    public boolean hasTranslucency(@Nullable List<NativeImage> quadNatives) {
        return SpriteUtils.doesHaveTranslucency(getUnmipmappedImage(), quadNatives);
    }

    @Override
    public boolean hasTranslucency(QuadBounds bounds, @Nullable List<NativeImage> quadNatives) {
        return SpriteUtils.doesHaveTranslucency(getUnmipmappedImage(), bounds, quadNatives);
    }
}
