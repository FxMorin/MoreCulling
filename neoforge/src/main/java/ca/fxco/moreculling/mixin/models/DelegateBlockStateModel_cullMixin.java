package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DelegateBlockStateModel.class)
public class DelegateBlockStateModel_cullMixin implements BakedOpacity {

    @Shadow
    @Final
    protected BlockStateModel delegate;

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        ((BakedOpacity) delegate).moreculling$resetTranslucencyCache(state);
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        return ((BakedOpacity) delegate).moreculling$getCullingShape(state);
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        ((BakedOpacity) delegate).moreculling$setCullingShape(cullingShape);
    }

    @Override
    public boolean moreculling$getHasAutoModelShape() {
        return ((BakedOpacity) delegate).moreculling$getHasAutoModelShape();
    }

    @Override
    public void moreculling$setHasAutoModelShape(boolean hasAutoModelShape) {
        ((BakedOpacity) delegate).moreculling$setHasAutoModelShape(hasAutoModelShape);
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return ((BakedOpacity) delegate).moreculling$canSetCullingShape();
    }
}
