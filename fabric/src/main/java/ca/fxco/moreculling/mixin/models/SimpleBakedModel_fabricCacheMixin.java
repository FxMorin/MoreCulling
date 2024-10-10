package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public abstract class SimpleBakedModel_fabricCacheMixin implements BakedOpacity {
    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void moreculling$onInit(List list, Map map, boolean bl, boolean bl2, boolean bl3,
                                    TextureAtlasSprite textureAtlasSprite,
                                    ItemTransforms itemTransforms, CallbackInfo ci) {
        moreculling$resetTranslucencyCache();
    }
}
