package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockStateModelPart.class)
public interface BlockStateModelPart_extendsMixin extends BakedOpacity {
}
