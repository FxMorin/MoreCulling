package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.platform.Services;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = MultiPartBakedModel.class, priority = 1010)
public abstract class MultiPartBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, BakedModel>> selectors;

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        byte emptyFaces = 0;
        boolean translucency = false;
        for (Direction face : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> quads = Services.PLATFORM.getQuads((BakedModel) this, state,
                    face, CullingUtils.RANDOM, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (quads.isEmpty()) { // no faces = translucent
                emptyFaces = BitUtils.set(emptyFaces, face.ordinal());
            } else if (!translucency) {
                for (BakedQuad quad : quads) {
                    if (((QuadOpacity) (Object) quad).moreculling$getTextureTranslucency()) {
                        translucency = true;
                        break;
                    }
                }
            }
        }

        ((MoreStateCulling) state).moreculling$setHasQuadsOnSide(emptyFaces);
        ((MoreStateCulling) state).moreculling$setHasTextureTranslucency(translucency);
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        VoxelShape cachedShape = null;
        for (Pair<Predicate<BlockState>, BakedModel> pair : this.selectors) {
            if ((pair.getLeft()).test(state)) {
                VoxelShape shape = ((BakedOpacity) pair.getRight()).moreculling$getCullingShape(state);
                if (shape != null) {
                    if (cachedShape == null) {
                        cachedShape = shape;
                    } else {
                        cachedShape = Shapes.or(cachedShape, shape);
                    }
                }
            }
        }
        return cachedShape;
    }
}
