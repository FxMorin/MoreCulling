package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedTransparency;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinBakedModel.class)
public abstract class BuiltinBakedModel_cacheMixin implements BakedTransparency {

    @Unique
    private boolean hasTransparency;

    @Override
    public boolean hasTextureTransparency() {
        return hasTransparency;
    }


    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInit(ModelTransformation transformation, ModelOverrideList itemPropertyOverrides,
                        Sprite sprite, boolean sideLit, CallbackInfo ci) {
        hasTransparency = SpriteUtils.doesHaveTransparency(sprite);
    }
}
