package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.platform.Services;
import ca.fxco.moreculling.utils.BitUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.multipart.MultiPartModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = MultiPartModel.class, priority = 1010)
public abstract class MultiPartModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    private MultiPartModel.SharedBakedState shared;


    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        byte emptyFaces = 0;
        boolean translucency = false;
        for (Direction face : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> quads = Services.PLATFORM.getQuads((BlockStateModel) this, state,
                    face, CullingUtils.RANDOM, EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO);
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
        for (MultiPartModel.Selector<BlockStateModel> selector : this.shared.selectors) {
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
