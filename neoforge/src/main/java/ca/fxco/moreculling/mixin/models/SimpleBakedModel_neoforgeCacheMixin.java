package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public abstract class SimpleBakedModel_neoforgeCacheMixin implements BakedOpacity {
    @Inject(
            method = "<init>(Ljava/util/List;Ljava/util/Map;" +
                    "ZZZLnet/minecraft/client/renderer/texture/TextureAtlasSprite;" +
                    "Lnet/minecraft/client/renderer/block/model/ItemTransforms;" +
                    "Lnet/neoforged/neoforge/client/RenderTypeGroup;)V",
            at = @At("RETURN")
    )
    private void moreculling$onInit(List p_119489_, Map p_119490_, boolean p_119491_, boolean p_119492_,
                                    boolean p_119493_, TextureAtlasSprite p_119494_, ItemTransforms p_119495_,
                                    RenderTypeGroup renderTypes, CallbackInfo ci) {
        moreculling$resetTranslucencyCache();
    }
}
