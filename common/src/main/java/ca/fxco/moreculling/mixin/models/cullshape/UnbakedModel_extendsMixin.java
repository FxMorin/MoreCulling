package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.ExtendedUnbakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(UnbakedModel.class)
public interface UnbakedModel_extendsMixin extends ExtendedUnbakedModel {
}
