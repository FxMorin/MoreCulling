package ca.fxco.moreculling.mixin.models;

import ca.fxco.moreculling.api.model.BakedOpacity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(require = @Condition("fabric-renderer-api-v1"))
@Mixin(ForwardingBakedModel.class)
public class ForwardingBakedModel_compatMixin implements BakedOpacity {

    @Shadow
    protected BakedModel wrapped;

    @Override
    public boolean hasTextureTranslucency(@Nullable BlockState state) {
        return ((BakedOpacity)wrapped).hasTextureTranslucency(state);
    }

    @Override
    public void resetTranslucencyCache() {
        ((BakedOpacity)wrapped).resetTranslucencyCache();
    }
}
