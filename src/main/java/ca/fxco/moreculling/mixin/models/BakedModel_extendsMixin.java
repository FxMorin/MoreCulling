package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public interface BakedModel_extendsMixin extends BakedOpacity {}

/*
TODO:
     - Attempt to move translucency checks so that the models are already chosen when the check is being made so
       we have a higher accuracy and no grouped or wrapped block models are used, instead the translucency check of the
       individual model is used which is much more accurate and much nicer to compute. This may be hard to do tho!
     - May need to make WeightedBakedModel dynamic so that if the wrapped models inside of the model are
       Multipart models we are able to pass the blockstate to them correctly instead of just having them return true.
       We can skip all that if we can get the first point work flawlessly
 */