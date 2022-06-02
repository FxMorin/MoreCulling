package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.patches.BakedTransparency;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(BuiltinBakedModel.class)
public class BuiltinBakedModel_cacheMixin implements BakedTransparency {

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
