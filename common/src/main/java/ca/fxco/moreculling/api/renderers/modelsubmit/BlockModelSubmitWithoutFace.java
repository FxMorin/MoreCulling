package ca.fxco.moreculling.api.renderers.modelsubmit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

import java.util.List;

public final class BlockModelSubmitWithoutFace extends MorecullingBlockModelSubmit {
    private final Direction withoutFace;
    public BlockModelSubmitWithoutFace(PoseStack.Pose pose, RenderType renderType,
                                       List<BlockStateModelPart> modelParts, int[] tintLayers,
                                       int lightCoords, int overlayCoords, int outlineColor,
                                       Object mesh, Direction withoutFace) {
        super(pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor, mesh);
        this.withoutFace = withoutFace;
    }

    @Override
    public boolean shouldCull(Direction face) {
        return face == withoutFace;
    }
}
