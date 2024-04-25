package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
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

@Mixin(BuiltinBakedModel.class)
public abstract class BuiltinBakedModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    private Sprite sprite;

    @Unique
    private boolean moreculling$hasTranslucency;
    @Unique
    private @Nullable VoxelShape moreculling$cullVoxelShape;

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return moreculling$hasTranslucency;
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        moreculling$hasTranslucency = ((SpriteOpacity) sprite).moreculling$hasTranslucency();
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
    private void moreculling$onInit(ModelTransformation transformation, ModelOverrideList itemPropertyOverrides,
                                    Sprite sprite, boolean sideLit, CallbackInfo ci) {
        moreculling$resetTranslucencyCache();
    }
}
