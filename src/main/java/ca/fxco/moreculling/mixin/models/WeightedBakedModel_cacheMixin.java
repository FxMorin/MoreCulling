package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(WeightedBakedModel.class)
public abstract class WeightedBakedModel_cacheMixin implements BakedOpacity {

    @Unique
    private boolean hasTranslucency;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return hasTranslucency;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = false;
    }

    @Inject(
            method = "getQuads",
            at = @At("RETURN")
    )
    private void onGetQuads(BlockState state, Direction direction, Random random,
                            CallbackInfoReturnable<List<BakedQuad>> cir) {
        hasTranslucency = false;
        for (BakedQuad quad : cir.getReturnValue()) {
            if (hasTranslucency |= ((QuadOpacity)quad).getTextureTranslucency()) break;
        }
    }
}
