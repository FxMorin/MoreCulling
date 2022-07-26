package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    public boolean hasTextureTranslucency() {
        return hasTranslucency;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = ((SpriteOpacity)this.sprite).hasTranslucency();
        if (!hasTranslucency) {
            for (BakedQuad baked : this.quads) {
                if (((SpriteOpacity)baked.getSprite()).hasTranslucency()) {
                    hasTranslucency = true;
                    break;
                }
            }
        }
        if (!hasTranslucency) {
            for (List<BakedQuad> bakedList : this.faceQuads.values()) {
                for (BakedQuad baked : bakedList) {
                    if (((SpriteOpacity)baked.getSprite()).hasTranslucency()) {
                        hasTranslucency = true;
                        break;
                    }
                }
                if (hasTranslucency) break;
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
