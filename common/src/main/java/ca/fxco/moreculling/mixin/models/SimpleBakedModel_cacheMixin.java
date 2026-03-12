package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
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
    @Shadow
    @Final
    protected List<BakedQuad> unculledFaces;
    @Unique
    private boolean moreculling$allStatesCanOcclude = true;
    @Unique
    private @Nullable VoxelShape moreculling$cullVoxelShape;
    @Unique
    private boolean moreculling$wasShapeOptimized = false;

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        DirectionBits emptyFaces = new DirectionBits();
        boolean translucency = false;

        for (Map.Entry<Direction, List<BakedQuad>> entry : culledFaces.entrySet()) {
            Direction direction = entry.getKey();
            List<BakedQuad> layeredQuads = new ArrayList<>(entry.getValue());
            if (!layeredQuads.isEmpty()) {
                if (!translucency) {
                    for (BakedQuad quad : layeredQuads) {
                        SpriteOpacity opacity = ((SpriteOpacity) quad.getSprite());
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

        if (!translucency) {
            for (BakedQuad quad : unculledFaces) {
                SpriteOpacity opacity = ((SpriteOpacity) quad.getSprite());
                NativeImage image = opacity.moreculling$getUnmipmappedImage();
                QuadBounds bounds = VertexUtils.getQuadUvBounds(quad, image.getWidth(), image.getHeight());
                if (opacity.moreculling$hasTranslucency(bounds)) {
                    translucency = true;
                }
            }
        }

        ((MoreStateCulling) state).moreculling$setHasQuadsOnSide(emptyFaces.getBits());
        ((MoreStateCulling) state).moreculling$setHasTextureTranslucency(translucency);
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
    public boolean moreculling$canSetCullingShape() {
        return true;
    }
}
