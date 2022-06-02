package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.patches.BakedTransparency;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultipartBakedModel.class)
public class MultipartBakedModel_cacheMixin implements BakedTransparency {

    @Override
    public boolean hasTransparency() {
        return false;
    }
}
