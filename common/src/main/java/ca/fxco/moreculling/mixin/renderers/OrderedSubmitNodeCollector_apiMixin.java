package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedOrderedSubmitNodeCollector;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OrderedSubmitNodeCollector.class)
public interface OrderedSubmitNodeCollector_apiMixin extends ExtendedOrderedSubmitNodeCollector {
}
