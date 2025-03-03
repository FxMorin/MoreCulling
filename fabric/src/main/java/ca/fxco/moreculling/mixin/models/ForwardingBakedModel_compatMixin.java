package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(require = @Condition("fabric-renderer-api-v1"))
@Mixin(ForwardingBakedModel.class)
public class ForwardingBakedModel_compatMixin implements BakedOpacity {

    @Shadow
    protected BakedModel wrapped;

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
    public boolean moreculling$canSetCullingShape() {
        return ((BakedOpacity) wrapped).moreculling$canSetCullingShape();
    }
}
