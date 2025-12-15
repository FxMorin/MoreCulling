package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = WeightedBakedModel.class, priority = 1010)
public abstract class WeightedBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    public abstract List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random);

    @Override
    public void resetTranslucencyCache(BlockState state) {
        byte emptyFaces = 0;
        boolean translucency = false;
        for (Direction face : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> quads = getQuads(state, face, CullingUtils.RANDOM);
            if (quads.isEmpty()) { // no faces = empty
                emptyFaces = BitUtils.set(emptyFaces, face.ordinal());
            } else if (!translucency) {
                for (BakedQuad quad : quads) {
                    if (((QuadOpacity) quad).getTextureTranslucency()) {
                        translucency = true;
                        break;
                    }
                }
            }
        }

        ((MoreStateCulling) state).moreculling$setHasQuadsOnSide(emptyFaces);
        ((MoreStateCulling) state).moreculling$setHasTextureTranslucency(translucency);
    }
}
