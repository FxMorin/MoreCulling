package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.DelegateBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DelegateBakedModel.class)
public class DelegateBakedModel_cullMixin implements BakedOpacity {

    @Shadow @Final protected BakedModel parent;

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return ((BakedOpacity) parent).moreculling$hasTextureTranslucency(state, direction);
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        ((BakedOpacity) parent).moreculling$resetTranslucencyCache();
    }

    @Override
    public void moreculling$initTranslucencyCache(BlockState state) {
        ((BakedOpacity) parent).moreculling$initTranslucencyCache(state);
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        return ((BakedOpacity) parent).moreculling$getCullingShape(state);
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        ((BakedOpacity) parent).moreculling$setCullingShape(cullingShape);
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return ((BakedOpacity) parent).moreculling$canSetCullingShape();
    }
}
