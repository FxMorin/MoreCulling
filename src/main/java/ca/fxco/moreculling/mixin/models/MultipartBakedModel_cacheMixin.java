package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.utils.BitUtils;
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

@Mixin(MultipartBakedModel.class)
public abstract class MultipartBakedModel_cacheMixin implements BakedOpacity {

    //TODO: Find a proper way to declare all Multipart Caches on game load instead of using `getQuads`

    @Shadow @Final
    private List<Pair<Predicate<BlockState>, BakedModel>> components;

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

    @Override
    public @Nullable VoxelShape getCullingShape(BlockState state) {
        VoxelShape cachedShape = null;
        for(Pair<Predicate<BlockState>, BakedModel> pair : this.components) {
            if ((pair.getLeft()).test(state)) {
                VoxelShape shape = ((BakedOpacity)pair.getRight()).getCullingShape(state);
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


    @Inject(
            method = "getQuads",
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
