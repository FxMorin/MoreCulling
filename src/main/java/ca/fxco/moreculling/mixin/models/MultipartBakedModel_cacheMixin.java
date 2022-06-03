package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.patches.BakedTransparency;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModel_cacheMixin implements BakedTransparency {

    @Override
    public boolean hasTransparency() {
        return false;
    }
}
