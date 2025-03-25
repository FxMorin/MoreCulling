package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SpriteGetter;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/* TODO
@Mixin(ItemModelGenerator.class)
public class ItemModelGenerator_cullMixin {
    @Inject(
            method = "bake(Lnet/minecraft/client/renderer/block/model/TextureSlots;Lnet/minecraft/client/resources/model/SpriteGetter;Lnet/minecraft/client/resources/model/ModelState;ZZLnet/minecraft/client/renderer/block/model/ItemTransforms;Lnet/neoforged/neoforge/client/RenderTypeGroup;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(
                    value = "RETURN"
            )
    )
    private void markAsItem(TextureSlots p_387202_, SpriteGetter p_387257_, ModelState p_387172_,
                            boolean p_388328_, boolean p_387288_, ItemTransforms p_388238_,
                            RenderTypeGroup renderTypes, CallbackInfoReturnable<BakedModel> cir) {
        ((BakedOpacity) cir.getReturnValue()).moreculling$setIsItem();
    }
}*/
