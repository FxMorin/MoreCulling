package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
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
public abstract class LayerRenderState_apiMixin implements ExtendedItemStackRenderState {
    @Shadow private int[] tintLayers;
    @Unique
    private boolean moreculling$blockItem = false;

    @Override
    public void moreculling$renderBakedItemModelWithoutFace(List<BakedQuad> quads, int light, int overlay,
                                                            PoseStack pose, VertexConsumer vertices,
                                                            @Nullable Direction withoutFace) {
        ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(withoutFace);
        ItemRenderer.renderQuadList(pose, vertices, quads, this.tintLayers, light, overlay);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderBakedItemModelOnly3Faces(List<BakedQuad> model, int light, int overlay,
                                                           PoseStack pose, VertexConsumer vertices,
                                                           Direction faceX, Direction faceY, Direction faceZ) {
        ItemRendererStates.DIRECTIONS = new Direction[] { faceX, faceY, faceZ };
        ItemRenderer.renderQuadList( pose, vertices, model, this.tintLayers, light, overlay);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$renderBakedItemModelForFace(List<BakedQuad> model, int light, int overlay,
                                                        PoseStack pose, VertexConsumer vertices,
                                                        Direction face) {
        ItemRendererStates.DIRECTIONS = new Direction[] { face };
        ItemRenderer.renderQuadList( pose, vertices, model, this.tintLayers, light, overlay);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public boolean moreculling$isBlockItem() {
        return moreculling$blockItem;
    }

    @Override
    public void moreculling$setIsBlockItem(boolean isBlockItem) {
        this.moreculling$blockItem = isBlockItem;
    }
}
