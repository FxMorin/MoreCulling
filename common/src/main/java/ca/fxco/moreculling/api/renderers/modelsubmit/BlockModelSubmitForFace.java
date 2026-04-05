package ca.fxco.moreculling.api.renderers.modelsubmit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

import java.util.List;

public final class BlockModelSubmitForFace extends MorecullingBlockModelSubmit {
    private final Direction forFace;
    public BlockModelSubmitForFace(PoseStack.Pose pose, RenderType renderType,
                                   List<BlockStateModelPart> modelParts, int[] tintLayers,
                                   int lightCoords, int overlayCoords, int outlineColor,
                                   Object mesh, Direction forFace) {
        super(pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor, mesh);
        this.forFace = forFace;
    }

    @Override
    public boolean shouldCull(Direction face) {
        return face != forFace;
    }
}
