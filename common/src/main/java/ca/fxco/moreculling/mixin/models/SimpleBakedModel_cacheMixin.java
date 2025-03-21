package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.utils.DirectionBits;
import ca.fxco.moreculling.utils.VertexUtils;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
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
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public abstract class SimpleBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    protected Map<Direction, List<BakedQuad>> culledFaces; // cullface quads

    @Unique
    private final DirectionBits moreculling$solidFaces = new DirectionBits();
    @Unique
    private boolean moreculling$allStatesCanOcclude = true;
    @Unique
    private @Nullable VoxelShape moreculling$cullVoxelShape;
    @Unique
    private @Nullable boolean moreculling$isItem = false;
    @Unique
    private @Nullable boolean moreculling$hasAutoModelShape = true;
    @Unique
    private boolean moreculling$wasShapeOptimized = false;

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

        for (Map.Entry<Direction, List<BakedQuad>> entry : culledFaces.entrySet()) {
            Direction direction = entry.getKey();
            List<BakedQuad> layeredQuads = new ArrayList<>(entry.getValue());
            if (!layeredQuads.isEmpty()) {
                BakedQuad initialQuad = layeredQuads.removeFirst();
                SpriteOpacity opacity = ((SpriteOpacity) initialQuad.getSprite());
                NativeImage image = opacity.moreculling$getUnmipmappedImage();
                QuadBounds bounds = VertexUtils.getQuadUvBounds(initialQuad, image.getWidth(), image.getHeight());
                if (!opacity.moreculling$hasTranslucency(bounds)) {
                    if (!layeredQuads.isEmpty()) {
                        List<NativeImage> overlappingImages = new ArrayList<>();
                        for (BakedQuad quad : layeredQuads) {
                            overlappingImages.add(((SpriteOpacity) quad.getSprite())
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
        if (!this.moreculling$wasShapeOptimized) {
            if (this.moreculling$cullVoxelShape != null) {
                this.moreculling$cullVoxelShape = moreculling$cullVoxelShape.optimize();
            }
            this.moreculling$wasShapeOptimized = true;
        }

        return this.moreculling$cullVoxelShape;
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        this.moreculling$cullVoxelShape = cullingShape;
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

    @Override
    public boolean moreculling$isItem() {
        return moreculling$isItem;
    }

    @Override
    public void moreculling$setIsItem() {
        moreculling$isItem = true;
    }
}
