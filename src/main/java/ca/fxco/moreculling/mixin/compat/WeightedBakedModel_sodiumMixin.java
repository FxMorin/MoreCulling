package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.utils.BitUtils;
import com.bawnorton.mixinsquared.TargetHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
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

@Restriction(require = @Condition("sodium"))
@Mixin(value = WeightedBakedModel.class, priority = 1010)
public abstract class WeightedBakedModel_sodiumMixin implements BakedOpacity {

    //TODO: Find a proper way to declare all Weighted Caches on game load instead of using `getQuads`

    @Unique // Only works on chunk update, so the best performance is after placing a block
    private byte solidFaces = 0; // 0 = all sides translucent

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState blockState, @Nullable Direction direction) {
        if (direction == null) {
            return solidFaces != BitUtils.ALL_DIRECTIONS; // If any translucency, returns true
        }
        return !BitUtils.get(solidFaces, direction.ordinal());
    }

    @Override
    public void resetTranslucencyCache() {
        solidFaces = 0;
    }


    @TargetHandler(
            mixin = "me.jellysquid.mods.sodium.mixin.features.model.WeightedBakedModelMixin",
            name = "getQuads"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("RETURN")
    )
    private void onGetQuads(@Nullable BlockState state, @Nullable Direction face, Random random,
                            CallbackInfoReturnable<List<BakedQuad>> cir) {
        if (face != null) { // Must be quads that have cullface
            List<BakedQuad> quads = cir.getReturnValue();
            if (quads.isEmpty()) { // no faces = translucent
                solidFaces = BitUtils.unset(solidFaces, face.ordinal());
            } else {
                solidFaces = BitUtils.set(solidFaces, face.ordinal());
                for (BakedQuad quad : quads) {
                    if (((QuadOpacity) quad).getTextureTranslucency()) {
                        solidFaces = BitUtils.unset(solidFaces, face.ordinal());
                        break;
                    }
                }
            }
        }
    }
}
