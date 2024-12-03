package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.states.ItemRendererStates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = IBakedModelExtension.class, priority = 1100)
public interface IBakedModelExtension_faceCullingMixin {

    @WrapOperation(
            method = "applyTransform",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(" +
                            "Lnet/minecraft/world/item/ItemDisplayContext;)" +
                            "Lnet/minecraft/client/renderer/block/model/ItemTransform;"
            )
    )
    private ItemTransform moreculling$getTransformation(
            ItemTransforms instance, ItemDisplayContext displayContext,
            Operation<ItemTransform> original
    ) {
        ItemTransform transformation = original.call(instance, displayContext);
        if (ItemRendererStates.ITEM_FRAME != null) {
            ItemRendererStates.TRANSFORMS = transformation;
        }
        return transformation;
    }
}
