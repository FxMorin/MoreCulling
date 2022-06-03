package ca.fxco.moreculling.mixin.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModel_cacheMixin implements BakedModel {

    @Override
    public boolean hasTransparency() {
        return false;
    }
}
