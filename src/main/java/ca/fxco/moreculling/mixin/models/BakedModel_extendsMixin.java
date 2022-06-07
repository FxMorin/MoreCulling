package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedTransparency;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public interface BakedModel_extendsMixin extends BakedTransparency {}
