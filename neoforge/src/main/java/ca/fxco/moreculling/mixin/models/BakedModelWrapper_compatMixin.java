package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BakedModelWrapper.class)
public class BakedModelWrapper_compatMixin implements BakedOpacity {

    @Final
    @Shadow
    protected BakedModel originalModel;

    @Override
    public boolean moreculling$hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return ((BakedOpacity) originalModel).moreculling$hasTextureTranslucency(state, direction);
    }

    @Override
    public void moreculling$resetTranslucencyCache() {
        ((BakedOpacity) originalModel).moreculling$resetTranslucencyCache();
    }

    @Override
    public @Nullable VoxelShape moreculling$getCullingShape(BlockState state) {
        return ((BakedOpacity) originalModel).moreculling$getCullingShape(state);
    }

    @Override
    public void moreculling$setCullingShape(VoxelShape cullingShape) {
        ((BakedOpacity) originalModel).moreculling$setCullingShape(cullingShape);
    }

    @Override
    public boolean moreculling$canSetCullingShape() {
        return ((BakedOpacity) originalModel).moreculling$canSetCullingShape();
    }
}
