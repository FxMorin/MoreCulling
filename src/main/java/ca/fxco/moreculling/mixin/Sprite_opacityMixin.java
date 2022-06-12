package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Sprite.class)
public class Sprite_opacityMixin implements SpriteOpacity {

    @Shadow
    @Final
    protected NativeImage[] images;

    @Override
    public boolean hasTransparency() {
        for (NativeImage nativeImage : this.images)
            if (SpriteUtils.doesHaveTransparency(nativeImage))
                return true;
        return false;
    }

    @Override
    public boolean hasTranslucency() {
        for (NativeImage nativeImage : this.images)
            if (SpriteUtils.doesHaveTranslucency(nativeImage))
                return true;
        return false;
    }
}
