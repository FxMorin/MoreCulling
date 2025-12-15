package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.utils.DirectionBits;
import ca.fxco.moreculling.utils.DirectionUtils;
import ca.fxco.moreculling.utils.VertexUtils;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SingleVariant;
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

    @Shadow @Final private BlockModelPart model;

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        DirectionBits emptyFaces = new DirectionBits();
        boolean translucency = false;

        for (Direction direction : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> layeredQuads = model.getQuads(direction);
            if (!layeredQuads.isEmpty()) {
                if (!translucency) {
                    for (BakedQuad quad : layeredQuads) {
                        SpriteOpacity opacity = ((SpriteOpacity) quad.sprite());
                        NativeImage image = opacity.moreculling$getUnmipmappedImage();
                        QuadBounds bounds = VertexUtils.getQuadUvBounds(quad, image.getWidth(), image.getHeight());
                        if (opacity.moreculling$hasTranslucency(bounds)) {
                            translucency = true;
                        }
                    }
                }
            } else {
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
