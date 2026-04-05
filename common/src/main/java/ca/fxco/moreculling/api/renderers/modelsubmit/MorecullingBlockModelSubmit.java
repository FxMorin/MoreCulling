package ca.fxco.moreculling.api.renderers.modelsubmit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.Objects;

public abstract class MorecullingBlockModelSubmit {

    private final PoseStack.Pose pose;
    private final RenderType renderType;
    private final List<BlockStateModelPart> modelParts;
    private final int[] tintLayers;
    private final int lightCoords;
    private final int overlayCoords;
    private final int outlineColor;
    private final Object mesh;

    public MorecullingBlockModelSubmit(
            PoseStack.Pose pose,
            RenderType renderType,
            List<BlockStateModelPart> modelParts,
            int[] tintLayers,
            int lightCoords,
            int overlayCoords,
            int outlineColor,
            Object mesh
    ) {
        this.pose = pose;
        this.renderType = renderType;
        this.modelParts = modelParts;
        this.tintLayers = tintLayers;
        this.lightCoords = lightCoords;
        this.overlayCoords = overlayCoords;
        this.outlineColor = outlineColor;
        this.mesh = mesh;
    }


    public abstract boolean shouldCull(Direction face);

    public PoseStack.Pose pose() {
        return pose;
    }

    public RenderType renderType() {
        return renderType;
    }

    public List<BlockStateModelPart> modelParts() {
        return modelParts;
    }

    public int[] tintLayers() {
        return tintLayers;
    }

    public int lightCoords() {
        return lightCoords;
    }

    public int overlayCoords() {
        return overlayCoords;
    }

    public int outlineColor() {
        return outlineColor;
    }

    public Object mesh() {
        return mesh;
    }
}
