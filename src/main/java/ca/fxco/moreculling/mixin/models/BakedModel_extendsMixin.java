package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public interface BakedModel_extendsMixin extends BakedOpacity {}

/*
TODO:
     - May need to make WeightedBakedModel dynamic so that if the wrapped models inside of the model are
       Multipart models we are able to pass the blockstate to them correctly instead of just having them return true.
 */