package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModel_cacheMixin implements BakedOpacity {

    // Shouldn't be used, since the models are generated per blockstate so the individual models will be used instead

    @Override
    public boolean hasTextureTranslucency() {
        return false;
    }
}
