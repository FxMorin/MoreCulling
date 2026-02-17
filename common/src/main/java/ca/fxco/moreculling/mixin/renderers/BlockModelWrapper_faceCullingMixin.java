package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModelWrapper.class)
public class BlockModelWrapper_faceCullingMixin {
    @Inject(
            method = "update",
            at = @At(value = "TAIL")
    )
    private void moreculling$checkIfBlockItem(ItemStackRenderState p_386488_, ItemStack stack,
                                              ItemModelResolver p_388726_, ItemDisplayContext p_388231_,
                                              ClientLevel p_387522_, ItemOwner p_434975_, int p_388300_,
                                              CallbackInfo ci, @Local(ordinal = 0)
                                                  ItemStackRenderState.LayerRenderState layerRenderState
    ) {
        ((ExtendedItemStackRenderState) layerRenderState).moreculling$setIsBlockItem(stack.getItem() instanceof BlockItem);
    }
}
