package ca.fxco.moreculling.api.renderers.modelsubmit;

import ca.fxco.moreculling.api.renderers.MorecullingBlockModelFeatureRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class MorecullingBlockModelSubmit implements TranslucentSubmit {

    private final PoseStack.Pose pose;
    private final RenderType renderType;
    private final List<BlockStateModelPart> modelParts;
    private final int[] tintLayers;
    private final int lightCoords;
    private final int overlayCoords;
    private final int tintColor;
    private final PoseStack.@Nullable Pose sheetedDecalPose;
    private final Object mesh;

    public MorecullingBlockModelSubmit(
            PoseStack.Pose pose,
            RenderType renderType,
            List<BlockStateModelPart> modelParts,
            int[] tintLayers,
            int lightCoords,
            int overlayCoords,
            int tintColor,
            PoseStack.@Nullable Pose sheetedDecalPose,
            Object mesh
    ) {
        this.pose = pose;
        this.renderType = renderType;
        this.modelParts = modelParts;
        this.tintLayers = tintLayers;
        this.lightCoords = lightCoords;
        this.overlayCoords = overlayCoords;
        this.tintColor = tintColor;
        this.sheetedDecalPose = sheetedDecalPose;
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

    public int tintColor() {
        return tintColor;
    }

    public PoseStack.Pose sheetedDecalPose() {
        return sheetedDecalPose;
    }

    public Object mesh() {
        return mesh;
    }

    @Override
    public float distanceToCameraSq() {
        return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose(), 0.5F, 0.5F, 0.5F);
    }

    @Override
    public FeatureRendererType<MorecullingBlockModelSubmit> featureType() {
        return MorecullingBlockModelFeatureRenderer.TYPE;
    }
}
