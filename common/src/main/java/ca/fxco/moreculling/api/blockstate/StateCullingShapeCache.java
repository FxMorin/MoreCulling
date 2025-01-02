package ca.fxco.moreculling.api.blockstate;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface StateCullingShapeCache {

    VoxelShape moreculling$getFaceCullingShape(Direction face);

}
