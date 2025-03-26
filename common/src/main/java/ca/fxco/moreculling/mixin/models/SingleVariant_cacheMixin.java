package ca.fxco.moreculling.mixin.models;

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
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(SingleVariant.class)
public abstract class SingleVariant_cacheMixin implements BakedOpacity {

    @Shadow @Final private BlockModelPart model;
    @Unique
    private final DirectionBits moreculling$solidFaces = new DirectionBits();

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return direction == null ? moreculling$solidFaces.notFull() : !moreculling$solidFaces.contains(direction);
    }

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        moreculling$solidFaces.clear();

        if (state.canOcclude()) {
            if (moreculling$allStatesCanOcclude) {
                moreculling$solidFaces.fill();
            }
        } else {
            moreculling$allStatesCanOcclude = false;
        }

        for (Direction direction : DirectionUtils.DIRECTIONS) {
            List<BakedQuad> layeredQuads = new ArrayList<>(model.getQuads(direction));
            if (!layeredQuads.isEmpty()) {
                BakedQuad initialQuad = layeredQuads.removeFirst();
                SpriteOpacity opacity = ((SpriteOpacity) initialQuad.sprite());
                NativeImage image = opacity.moreculling$getUnmipmappedImage();
                QuadBounds bounds = VertexUtils.getQuadUvBounds(initialQuad, image.getWidth(), image.getHeight());
                if (!opacity.moreculling$hasTranslucency(bounds)) {
                    if (!layeredQuads.isEmpty()) {
                        List<NativeImage> overlappingImages = new ArrayList<>();
                        for (BakedQuad quad : layeredQuads) {
                            overlappingImages.add(((SpriteOpacity) quad.sprite())
                                    .moreculling$getUnmipmappedImage());
                        }
                        if (!opacity.moreculling$hasTranslucency(bounds, overlappingImages)) {
                            moreculling$solidFaces.add(direction);
                        }
                    } else {
                        moreculling$solidFaces.add(direction);
                    }
                }
            }
        }
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
    public boolean moreculling$getHasAutoModelShape() {
        return moreculling$hasAutoModelShape;
    }

    @Override
    public void moreculling$setHasAutoModelShape(boolean hasAutoModelShape) {
        moreculling$hasAutoModelShape = hasAutoModelShape;
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return true;
    }
}
