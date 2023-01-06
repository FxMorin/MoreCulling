package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
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
    private final Set<Direction> translucentFaces = new HashSet<>(); //TODO: Make bitset

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        if (translucentFaces.size() > 0) {
            return direction == null || translucentFaces.contains(direction);
        }
        return false;
    }

    @Override
    public void resetTranslucencyCache() {
        translucentFaces.clear();
        for (Map.Entry<Direction, List<BakedQuad>> entry : faceQuads.entrySet()) {
            List<BakedQuad> layeredQuads = new ArrayList<>(entry.getValue());
            if (layeredQuads.size() > 0) {
                SpriteOpacity opacity = ((SpriteOpacity) layeredQuads.remove(0).getSprite());
                if (opacity.hasTranslucency()) {
                    translucentFaces.add(entry.getKey());
                } else {
                    List<NativeImage> overlappingImages = new ArrayList<>();
                    for (BakedQuad quad : layeredQuads) {
                        overlappingImages.add(((SpriteOpacity) quad.getSprite()).getUnmipmappedImage());
                    }
                    if (opacity.hasTranslucency(overlappingImages)) {
                        translucentFaces.add(entry.getKey());
                    }
                }
            } else {
                translucentFaces.add(entry.getKey());
            }
        }
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInit(List<BakedQuad> quads, Map<Direction, List<BakedQuad>> faceQuads, boolean usesAo,
                        boolean isSideLit, boolean hasDepth, Sprite sprite, ModelTransformation transformation,
                        ModelOverrideList itemPropertyOverrides, CallbackInfo ci) {
        resetTranslucencyCache();
    }
}
