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
    private @Nullable VoxelShape moreculling$cullVoxelShape;

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return direction == null ? moreculling$solidFaces.notFull() : !moreculling$solidFaces.contains(direction);
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        moreculling$solidFaces.clear();
        for (Map.Entry<Direction, List<BakedQuad>> entry : culledFaces.entrySet()) {
            Direction direction = entry.getKey();
            List<BakedQuad> layeredQuads = new ArrayList<>(entry.getValue());
            if (!layeredQuads.isEmpty()) {
                BakedQuad initialQuad = layeredQuads.removeFirst();
                SpriteOpacity opacity = ((SpriteOpacity) initialQuad.getSprite());
                QuadBounds bounds = VertexUtils.getQuadBounds(initialQuad, direction.getAxis());
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
