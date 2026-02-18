package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedItemStackRenderState;
import ca.fxco.moreculling.states.ItemRendererStates;
import ca.fxco.moreculling.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackRenderState.class)
public abstract class ItemStackRenderState_apiMixin implements ExtendedItemStackRenderState {

    @Shadow
    public abstract void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, int outlineColor);


    @Override
    public void moreculling$submitBakedItemModelWithoutFace(PoseStack poseStack, SubmitNodeCollector nodeCollector,
                                                            int packedLight, int packedOverlay, int outlineColor,
                                                            @Nullable Direction withoutFace) {
        ItemRendererStates.DIRECTIONS = DirectionUtils.getAllDirectionsExcluding(withoutFace);
        this.submit(poseStack, nodeCollector, packedLight, packedOverlay, outlineColor);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$submitBakedItemModelOnly3Faces(PoseStack poseStack, SubmitNodeCollector nodeCollector,
                                                           int packedLight, int packedOverlay, int outlineColor,
                                                           Direction faceX, Direction faceY, Direction faceZ) {
        ItemRendererStates.DIRECTIONS = new Direction[] { faceX, faceY, faceZ };
        this.submit(poseStack, nodeCollector, packedLight, packedOverlay, outlineColor);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$submitBakedItemModelForFace(PoseStack poseStack, SubmitNodeCollector nodeCollector,
                                                        int packedLight, int packedOverlay, int outlineColor,
                                                        Direction face) {
        ItemRendererStates.DIRECTIONS = new Direction[] { face };
        this.submit(poseStack, nodeCollector, packedLight, packedOverlay, outlineColor);
        ItemRendererStates.DIRECTIONS = null;
    }

    @Override
    public void moreculling$submitItemFrameItem(PoseStack pose, SubmitNodeCollector multiBufferSource,
                                                int light, ItemFrameRenderState frame, Camera camera) {
        ItemRendererStates.ITEM_FRAME = frame;
        ItemRendererStates.CAMERA = camera;
        this.submit(pose, multiBufferSource, light, OverlayTexture.NO_OVERLAY, frame.outlineColor);
        ItemRendererStates.ITEM_FRAME = null;
        ItemRendererStates.CAMERA = null;
    }
}
