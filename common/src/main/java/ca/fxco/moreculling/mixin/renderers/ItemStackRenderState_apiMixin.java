package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import ca.fxco.moreculling.states.ItemRendererStates;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackRenderState.class)
public abstract class ItemStackRenderState_apiMixin implements ExtendedItemStackRenderState {
    @Shadow public abstract void render(PoseStack p_388193_, MultiBufferSource p_388719_, int p_386913_, int p_387272_);

    @Override
    public void moreculling$renderItemFrameItem(PoseStack pose, MultiBufferSource multiBufferSource,
                                                int light, ItemFrameRenderState frame, Camera camera) {
        ItemRendererStates.ITEM_FRAME = frame;
        ItemRendererStates.CAMERA = camera;
        this.render(pose, multiBufferSource, light, OverlayTexture.NO_OVERLAY);
        ItemRendererStates.ITEM_FRAME = null;
        ItemRendererStates.CAMERA = null;
    }
}
