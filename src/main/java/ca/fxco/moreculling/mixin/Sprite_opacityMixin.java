package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Sprite.class)
public class Sprite_opacityMixin implements SpriteOpacity {

    @Shadow
    @Final
    protected NativeImage[] images;

    @Override
    public NativeImage getUnmipmappedImage() {
        return this.images[0];
    }

    @Override
    public boolean hasTransparency() {
        return SpriteUtils.doesHaveTransparency(getUnmipmappedImage());
    }

    @Override
    public boolean hasTranslucency() {
        return hasTranslucency(null);
    }

    @Override
    public boolean hasTranslucency(@Nullable List<NativeImage> quadNatives) {
        return SpriteUtils.doesHaveTranslucency(getUnmipmappedImage(), quadNatives);
    }
}
