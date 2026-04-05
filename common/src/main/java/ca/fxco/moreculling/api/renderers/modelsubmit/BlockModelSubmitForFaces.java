package ca.fxco.moreculling.api.renderers.modelsubmit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

import java.util.List;

public final class BlockModelSubmitForFaces extends MorecullingBlockModelSubmit {
    private final Direction[] faces;
    public BlockModelSubmitForFaces(PoseStack.Pose pose, RenderType renderType,
                                    List<BlockStateModelPart> modelParts, int[] tintLayers,
                                    int lightCoords, int overlayCoords, int outlineColor,
                                    Object mesh, Direction[] faces
    ) {
        super(pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor, mesh);
        this.faces = faces;
    }

    @Override
    public boolean shouldCull(Direction face) {
        for (Direction direction : faces) {
            if (face == direction) {
                return false;
            }
        }
        return true;
    }
}
