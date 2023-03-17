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
    private boolean hasTranslucency;
    @Unique
    private @Nullable VoxelShape cullVoxelShape;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return hasTranslucency;
    }

    @Override
    public void resetTranslucencyCache() {
        hasTranslucency = ((SpriteOpacity)sprite).hasTranslucency();
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

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onInit(ModelTransformation transformation, ModelOverrideList itemPropertyOverrides,
                        Sprite sprite, boolean sideLit, CallbackInfo ci) {
        resetTranslucencyCache();
    }
}
