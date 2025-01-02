package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.platform.Services;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = MultiPartBakedModel.class, priority = 1010)
public abstract class MultiPartBakedModel_cacheMixin implements BakedOpacity {

    //TODO: Find a proper way to declare all Multipart Caches on game load instead of using `getQuads`
    //TODO: test how good moreculling$initTranslucencyCache works

    @Shadow
    @Final
    private List<MultiPartBakedModel.Selector> selectors;

    @Unique // Only works on chunk update, so the best performance is after placing a block
    private byte solidFaces = 0; // 0 = all sides translucent

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState blockState, @Nullable Direction direction) {
        if (direction == null) {
            return solidFaces != BitUtils.ALL_DIRECTIONS; // If any translucency, returns true
        }
        return !BitUtils.get(solidFaces, direction.ordinal());
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        solidFaces = 0;
    }

    @Override
    public void moreculling$initTranslucencyCache(BlockState state) {
        for (Direction face : Direction.values()) {
            List<BakedQuad> quads = Services.PLATFORM.getQuads((BakedModel) this, state,
                    face, CullingUtils.RANDOM, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (quads.isEmpty()) { // no faces = translucent
                solidFaces = BitUtils.unset(solidFaces, face.ordinal());
            } else {
                solidFaces = BitUtils.set(solidFaces, face.ordinal());
                for (BakedQuad quad : quads) {
                    if (((QuadOpacity) quad).moreculling$getTextureTranslucency()) {
                        solidFaces = BitUtils.unset(solidFaces, face.ordinal());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        VoxelShape cachedShape = null;
        for (MultiPartBakedModel.Selector selector : this.selectors) {
            if ((selector.condition()).test(state)) {
                VoxelShape shape = ((BakedOpacity) selector.model()).moreculling$getCullingShape(state);
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
