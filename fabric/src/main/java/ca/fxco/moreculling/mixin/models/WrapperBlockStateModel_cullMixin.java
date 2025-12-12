package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
//import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel; TODO enable once ported
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(require = @Condition("fabric-model-loading-api-v1"))
//@Mixin(WrapperBlockStateModel.class)
public class WrapperBlockStateModel_cullMixin implements BakedOpacity {

    @Shadow
    protected BlockStateModel wrapped;

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return ((BakedOpacity) wrapped).moreculling$hasTextureTranslucency(state, direction);
    }

    @Override
    public void moreculling$resetTranslucencyCache(BlockState state) {
        ((BakedOpacity) wrapped).moreculling$resetTranslucencyCache(state);
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        return ((BakedOpacity) wrapped).moreculling$getCullingShape(state);
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        ((BakedOpacity) wrapped).moreculling$setCullingShape(cullingShape);
    }

    @Override
    public boolean moreculling$getHasAutoModelShape() {
        return ((BakedOpacity) wrapped).moreculling$getHasAutoModelShape();
    }

    @Override
    public void moreculling$setHasAutoModelShape(boolean hasAutoModelShape) {
        ((BakedOpacity) wrapped).moreculling$setHasAutoModelShape(hasAutoModelShape);
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return ((BakedOpacity) wrapped).moreculling$canSetCullingShape();
    }
}
