package ca.fxco.moreculling.states;

import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to allow MoreCulling to better share values to the item renderer and between the methods
 * within the item renderer. It needs to be state based in order to not break other mods by replacing all the logic,
 * like MoreCulling use to do.
 */
public class ItemRendererStates {

    public static @Nullable ItemFrame ITEM_FRAME;
    public static Camera CAMERA;
    public static Direction[] DIRECTIONS;
}
