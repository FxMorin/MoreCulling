package ca.fxco.moreculling.states;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to allow MoreCulling to better share values to the item renderer and between the methods
 * within the item renderer. It needs to be state based in order to not break other mods by replacing all the logic,
 * like MoreCulling use to do.
 */
public class ItemRendererStates {

    public static @Nullable ItemFrameRenderState ITEM_FRAME;
    public static Camera CAMERA;
    public static Direction[] DIRECTIONS;
    public static @Nullable ItemTransform TRANSFORMS;
}
