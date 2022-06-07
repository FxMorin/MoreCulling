package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedTransparency;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModel_cacheMixin implements BakedTransparency {

    @Override
    public boolean hasTextureTransparency() {
        return false;
    }
}
