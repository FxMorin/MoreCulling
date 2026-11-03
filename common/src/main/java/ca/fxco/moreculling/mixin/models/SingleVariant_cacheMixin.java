package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
import ca.fxco.moreculling.utils.DirectionBits;
import ca.fxco.moreculling.utils.DirectionUtils;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SingleVariant.class)
public abstract class SingleVariant_cacheMixin implements BakedOpacity {

    @Shadow @Final private BlockStateModelPart model;

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        DirectionBits emptyFaces = new DirectionBits();
        boolean translucency = false;

        for (Direction direction : DirectionUtils.DIRECTIONS_AND_NULL) {
            List<BakedQuad> layeredQuads = model.getQuads(direction);
            if (!layeredQuads.isEmpty()) {
                if (!translucency) {
                    for (BakedQuad quad : layeredQuads) {
                        if (((QuadOpacity) (Object) quad).moreculling$getTextureTranslucency()) {
                            translucency = true;
                            break;
                        }
                    }
                }
            } else if (direction != null) {
                emptyFaces.add(direction);
            }
        }

        ((MoreStateCulling) state).moreculling$setHasQuadsOnSide(emptyFaces.getBits());
        ((MoreStateCulling) state).moreculling$setHasTextureTranslucency(translucency);
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        return ((BakedOpacity) this.model).moreculling$getCullingShape(state);
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        ((BakedOpacity) this.model).moreculling$setCullingShape(cullingShape);
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return true;
    }

    @Override
    public boolean moreculling$getHasAutoModelShape() {
        return ((BakedOpacity) this.model).moreculling$getHasAutoModelShape();
    }

    @Override
    public void moreculling$setHasAutoModelShape(boolean hasAutoModelShape) {
        ((BakedOpacity) this.model).moreculling$setHasAutoModelShape(hasAutoModelShape);
    }
}
