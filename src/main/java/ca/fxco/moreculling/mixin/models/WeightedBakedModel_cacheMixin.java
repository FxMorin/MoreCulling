package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.Weighted;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WeightedBakedModel.class)
public abstract class WeightedBakedModel_cacheMixin implements BakedModel {

    @Shadow
    @Final
    private BakedModel defaultModel;

    @Unique
    private boolean hasTransparency;

    @Override
    public boolean hasTransparency() {
        return hasTransparency;
    }


    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInit(List<Weighted.Present<BakedModel>> models, CallbackInfo ci) {
        hasTransparency = SpriteUtils.doesHaveTransparency(this.defaultModel.getParticleSprite());
        if (!hasTransparency)
            for (Weighted.Present<BakedModel> bakedModelPresent : models)
                hasTransparency |= bakedModelPresent.getData().hasTransparency();
    }
}
