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
    public NativeImage[] getImages() {
        return this.images;
    }

    @Override
    public boolean hasTransparency() {
        for (NativeImage nativeImage : getImages())
            if (SpriteUtils.doesHaveTransparency(nativeImage))
                return true;
        return false;
    }

    @Override
    public boolean hasTranslucency() {
        return hasTranslucency(null);
    }

    @Override
    public boolean hasTranslucency(@Nullable List<NativeImage[]> quadNatives) {
        for (NativeImage nativeImage : getImages())
            if (SpriteUtils.doesHaveTranslucency(nativeImage, quadNatives))
                return true;
        return false;
    }
}
