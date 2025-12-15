package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
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
    private @Nullable VoxelShape cullVoxelShape;

    @Override
    public void resetTranslucencyCache(BlockState state) {
        DirectionBits emptyFaces = new DirectionBits();
        boolean translucency = false;

        for (Map.Entry<Direction, List<BakedQuad>> entry : faceQuads.entrySet()) {
            Direction direction = entry.getKey();
            List<BakedQuad> layeredQuads = entry.getValue();
            if (!layeredQuads.isEmpty()) {
                if (!translucency) {
                    for (BakedQuad quad : layeredQuads) {
                        SpriteOpacity opacity = ((SpriteOpacity) quad.getSprite());
                        NativeImage image = opacity.getUnmipmappedImage();
                        QuadBounds bounds = VertexUtils.getQuadBounds(quad, image.getWidth(), image.getHeight());
                        if (opacity.hasTranslucency(bounds)) {
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
    public @Nullable VoxelShape getCullingShape(BlockState state) {
        return this.cullVoxelShape;
    }

    @Override
    public void setCullingShape(VoxelShape cullingShape) {
        this.cullVoxelShape = cullingShape;
    }

    @Override
    public boolean canSetCullingShape() {
        return true;
    }
}
