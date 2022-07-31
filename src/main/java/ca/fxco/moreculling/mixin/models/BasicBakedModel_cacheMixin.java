package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public boolean hasTextureTranslucency(@Nullable BlockState state) {
        return hasTranslucency;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = ((SpriteOpacity)sprite).hasTranslucency();
        for (BakedQuad quad : quads) {
            if (((SpriteOpacity)quad.getSprite()).hasTranslucency()) {
                List<NativeImage> quadNatives = quads.stream().map((q) ->
                        ((SpriteOpacity)q.getSprite()).getUnmipmappedImage()
                ).collect(Collectors.toList());
                for (List<BakedQuad> bakedList : faceQuads.values()) {
                    for (BakedQuad baked : bakedList) {
                        if (((SpriteOpacity)baked.getSprite()).hasTranslucency(quadNatives)) {
                            hasTranslucency = true;
                            return;
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public List<BakedModel> getModels() {
        return List.of((BakedModel)this);
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
