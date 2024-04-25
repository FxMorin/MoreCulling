package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.data.QuadBounds;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import ca.fxco.moreculling.utils.DirectionBits;
import ca.fxco.moreculling.utils.VertexUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(BasicBakedModel.class)
public abstract class BasicBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    protected Map<Direction, List<BakedQuad>> faceQuads; // cullface quads

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
        for (Map.Entry<Direction, List<BakedQuad>> entry : faceQuads.entrySet()) {
            Direction direction = entry.getKey();
            List<BakedQuad> layeredQuads = new ArrayList<>(entry.getValue());
            if (!layeredQuads.isEmpty()) {
                BakedQuad initialQuad = layeredQuads.remove(0);
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

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void moreculling$onInit(List<BakedQuad> quads, Map<Direction, List<BakedQuad>> faceQuads, boolean usesAo,
                                    boolean isSideLit, boolean hasDepth, Sprite sprite,
                                    ModelTransformation transformation, ModelOverrideList itemPropertyOverrides,
                                    CallbackInfo ci) {
        moreculling$resetTranslucencyCache();
    }
}
