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
    public NativeImage moreculling$getUnmipmappedImage() {
        return ((SpriteContentsAccessor) contents).getImage();
    }

    @Override
    public boolean moreculling$hasTransparency() {
        return SpriteUtils.doesHaveTransparency(moreculling$getUnmipmappedImage());
    }

    @Override
    public boolean moreculling$hasTransparency(QuadBounds bounds) {
        return SpriteUtils.doesHaveTransparency(moreculling$getUnmipmappedImage());
    }

    @Override
    public boolean moreculling$hasTranslucency() {
        return moreculling$hasTranslucency((List<NativeImage>)null);
    }

    @Override
    public boolean moreculling$hasTranslucency(QuadBounds bounds) {
        return moreculling$hasTranslucency(bounds, null);
    }

    @Override
    public boolean moreculling$hasTranslucency(@Nullable List<NativeImage> quadNatives) {
        return SpriteUtils.doesHaveTranslucency(moreculling$getUnmipmappedImage(), quadNatives);
    }

    @Override
    public boolean moreculling$hasTranslucency(QuadBounds bounds, @Nullable List<NativeImage> quadNatives) {
        return SpriteUtils.doesHaveTranslucency(moreculling$getUnmipmappedImage(), bounds, quadNatives);
    }
}
