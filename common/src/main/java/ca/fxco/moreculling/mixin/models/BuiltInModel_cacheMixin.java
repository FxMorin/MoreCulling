package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.sprite.SpriteOpacity;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInModel.class)
public abstract class BuiltInModel_cacheMixin implements BakedOpacity {

    @Shadow
    @Final
    private TextureAtlasSprite particleTexture;

    @Unique
    private boolean moreculling$hasTranslucency;
    @Unique
    private @Nullable VoxelShape moreculling$cullVoxelShape;

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return moreculling$hasTranslucency;
    }

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        moreculling$hasTranslucency = ((SpriteOpacity) particleTexture).moreculling$hasTranslucency();
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
