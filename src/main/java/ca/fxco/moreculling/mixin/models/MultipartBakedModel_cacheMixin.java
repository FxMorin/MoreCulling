package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = MultipartBakedModel.class, priority = 1010)
public abstract class MultipartBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, BakedModel>> components;

    @Shadow
    public abstract List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random);

    @Override
    public void resetTranslucencyCache(BlockState state) {
        byte emptyFaces = 0;
        boolean translucency = false;
        for (Direction face : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> quads = getQuads(state, face, CullingUtils.RANDOM);
            if (quads.isEmpty()) { // no faces = translucent
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

    @Override
    public @Nullable VoxelShape getCullingShape(BlockState state) {
        VoxelShape cachedShape = null;
        for (Pair<Predicate<BlockState>, BakedModel> pair : this.components) {
            if ((pair.getLeft()).test(state)) {
                VoxelShape shape = ((BakedOpacity) pair.getRight()).getCullingShape(state);
                if (shape != null) {
                    if (cachedShape == null) {
                        cachedShape = shape;
                    } else {
                        cachedShape = VoxelShapes.union(cachedShape, shape);
                    }
                }
            }
        }
        return cachedShape;
    }
}
