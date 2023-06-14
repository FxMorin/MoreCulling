package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(require = @Condition("fabric-renderer-api-v1"))
@Mixin(ForwardingBakedModel.class)
public class ForwardingBakedModel_compatMixin implements BakedOpacity {

    @Shadow
    protected BakedModel wrapped;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state, @Nullable Direction direction) {
        return ((BakedOpacity)wrapped).hasTextureTranslucency(state, direction);
    }

    @Override
    public void resetTranslucencyCache() {
        ((BakedOpacity)wrapped).resetTranslucencyCache();
    }

    @Override
    public @Nullable VoxelShape getCullingShape(BlockState state) {
        return ((BakedOpacity)wrapped).getCullingShape(state);
    }

    @Override
    public void setCullingShape(VoxelShape cullingShape) {
        ((BakedOpacity)wrapped).setCullingShape(cullingShape);
    }

    @Override
    public boolean canSetCullingShape() {
        return ((BakedOpacity)wrapped).canSetCullingShape();
    }
}
