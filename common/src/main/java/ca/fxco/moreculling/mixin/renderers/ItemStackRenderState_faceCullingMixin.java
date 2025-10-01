package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.states.ItemRendererStates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemStackRenderState.LayerRenderState.class, priority = 1100)
public class ItemStackRenderState_faceCullingMixin {

    @WrapOperation(
            method = "submit",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState;" +
                            "transform:Lnet/minecraft/client/renderer/block/model/ItemTransform;"
            )
    )
    private ItemTransform moreculling$getTransformation(
            ItemStackRenderState.LayerRenderState instance, Operation<ItemTransform> original
    ) {
        ItemTransform transformation = original.call(instance);
        if (ItemRendererStates.ITEM_FRAME != null) {
            ItemRendererStates.TRANSFORMS = transformation;
        }
        return transformation;
    }
}
