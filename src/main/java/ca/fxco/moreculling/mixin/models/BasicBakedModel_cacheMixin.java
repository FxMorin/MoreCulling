package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.quad.QuadOpacity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(BasicBakedModel.class)
public abstract class BasicBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    protected Sprite sprite;

    @Shadow
    @Final
    protected List<BakedQuad> quads;

    @Shadow
    @Final
    protected Map<Direction, List<BakedQuad>> faceQuads;

    @Unique
    private boolean hasTranslucency;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        if (hasTranslucency) {
            if (direction != null) {
                return ((QuadOpacity)this.faceQuads.get(direction)).getTextureTranslucency();
            }
            return true;
        }
        return false;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = ((SpriteOpacity)sprite).hasTranslucency();
        if (!hasTranslucency) {
            List<NativeImage> quadNatives = null;
            for (BakedQuad quad : quads) {
                if (((QuadOpacity)quad).getTextureTranslucency()) {
                    if (quadNatives == null) { // make list of all faces sprites that could be overlapped
                        quadNatives = new ArrayList<>();
                        for (BakedQuad quad2 : quads) {
                            quadNatives.add(((SpriteOpacity) quad2.getSprite()).getUnmipmappedImage());
                        }
                    }
                    for (List<BakedQuad> bakedList : faceQuads.values()) { // Test
                        for (BakedQuad baked : bakedList) {
                            if (((SpriteOpacity)baked.getSprite()).hasTranslucency(quadNatives)) {
                                hasTranslucency = true;
                                return;
                            }
                        }
                    }
                }
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
