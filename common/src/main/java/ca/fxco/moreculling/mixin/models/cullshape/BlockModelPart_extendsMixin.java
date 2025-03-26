package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.resources.model.UnbakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockModelPart.class)
public interface BlockModelPart_extendsMixin extends BakedOpacity {
}
