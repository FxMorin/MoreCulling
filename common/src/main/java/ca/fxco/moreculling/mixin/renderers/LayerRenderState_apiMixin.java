package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class LayerRenderState_apiMixin implements ExtendedItemStackRenderState {
    @Shadow private int[] tintLayers;

    @Override
    public void moreculling$renderBakedItemModelWithoutFace(BakedModel model, int light, int overlay,
                                                            PoseStack pose, VertexConsumer vertices,
                                                            @Nullable Direction withoutFace) {
        ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(withoutFace);
        ItemRenderer.renderModelLists(model, this.tintLayers, light, overlay, pose, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderBakedItemModelOnly3Faces(BakedModel model, int light, int overlay,
                                                           PoseStack pose, VertexConsumer vertices,
                                                           Direction faceX, Direction faceY, Direction faceZ) {
        ItemRendererStates.DIRECTIONS = new Direction[] { faceX, faceY, faceZ };
        ItemRenderer.renderModelLists(model, this.tintLayers, light, overlay, pose, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderBakedItemModelForFace(BakedModel model, int light, int overlay,
                                                        PoseStack pose, VertexConsumer vertices,
                                                        Direction face) {
        ItemRendererStates.DIRECTIONS = new Direction[] { face };
        ItemRenderer.renderModelLists(model, this.tintLayers, light, overlay, pose, vertices);
        ItemRendererStates.DIRECTIONS = null;
    }
}
