package ca.fxco.moreculling.api.renderers.modelsubmit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

import java.util.List;

public final class BlockModelSubmitFor3Faces extends MorecullingBlockModelSubmit {
    private final Direction faceX;
    private final Direction faceY;
    private final Direction faceZ;
    public BlockModelSubmitFor3Faces(PoseStack.Pose pose, RenderType renderType,
                                     List<BlockStateModelPart> modelParts, int[] tintLayers,
                                     int lightCoords, int overlayCoords, int outlineColor,
                                     Object mesh, Direction faceX, Direction faceY, Direction faceZ
    ) {
        super(pose, renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor, mesh);
        this.faceX = faceX;
        this.faceY = faceY;
        this.faceZ = faceZ;
    }

    @Override
    public boolean shouldCull(Direction face) {
        return face != faceX && face != faceY && face != faceZ;
    }
}
