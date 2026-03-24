package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.platform.Services;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.WeightedVariants;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = WeightedVariants.class, priority = 1010)
public abstract class WeightedVariants_cacheMixin implements BakedOpacity {

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        byte emptyFaces = 0;
        boolean translucency = false;
        for (Direction face : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> quads = Services.PLATFORM.getQuads((BlockStateModel) this, state,
                    face, CullingUtils.RANDOM, BlockAndTintGetter.EMPTY, BlockPos.ZERO);
            if (quads.isEmpty()) { // no faces = empty
                emptyFaces = BitUtils.set(emptyFaces, face.ordinal());
            } else if (!translucency) {
                for (BakedQuad quad : quads) {
                    if (((QuadOpacity)  (Object) quad).moreculling$getTextureTranslucency()) {
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
