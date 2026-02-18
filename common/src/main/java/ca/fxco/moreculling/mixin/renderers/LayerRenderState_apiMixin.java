package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import ca.fxco.moreculling.api.renderers.ExtendedLayerRenderState;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class LayerRenderState_apiMixin implements ExtendedLayerRenderState {
    @Unique
    private boolean moreculling$blockItem = false;

    @Override
    public boolean moreculling$isBlockItem() {
        return moreculling$blockItem;
    }

    @Override
    public void moreculling$setIsBlockItem(boolean isBlockItem) {
        this.moreculling$blockItem = isBlockItem;
    }
}
