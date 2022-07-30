package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.SpriteUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.Weighted;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(WeightedBakedModel.class)
public abstract class WeightedBakedModel_cacheMixin implements BakedOpacity {

    /*
    All bakedModels will be merged together for the translucency check since there is no way to tell which models
    will be used when calling the translucency check. Should be fixed by never calling translucency checks for this
    model. Check BakedModel_extendsMixin for more info
     */


    @Shadow
    @Final
    private BakedModel defaultModel;

    @Shadow
    @Final
    private List<Weighted.Present<BakedModel>> models;

    @Unique
    private boolean hasTranslucency;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state) {
        return hasTranslucency;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = SpriteUtils.doesHaveTransparency(defaultModel.getParticleSprite());
        if (!hasTranslucency) {
            for (Weighted.Present<BakedModel> bakedModelPresent : models) {
                hasTranslucency = ((BakedOpacity) bakedModelPresent.getData()).hasTextureTranslucency(null);
                if (hasTranslucency) break;
            }
        }
    }

    @Override
    public List<BakedModel> getModels() {
        List<BakedModel> list = this.models.stream().map(Weighted.Present::getData).collect(Collectors.toList());
        list.add(this.defaultModel);
        return list;
    }


    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInit(List<Weighted.Present<BakedModel>> models, CallbackInfo ci) {
        resetTranslucencyCache();
    }
}
