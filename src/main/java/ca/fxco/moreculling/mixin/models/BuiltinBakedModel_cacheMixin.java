package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinBakedModel.class)
public abstract class BuiltinBakedModel_cacheMixin implements BakedModel {

    private boolean hasTransparency;

    @Override
    public boolean hasTransparency() {
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
