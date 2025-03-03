package ca.fxco.moreculling.utils;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ShapeUtils {
    private static final int[] DIR_ROT_X_2D_DATA = Util.make(new int[6], arr -> {
        arr[Direction.DOWN.ordinal()] = 2;
        arr[Direction.UP.ordinal()] = 0;
        arr[Direction.NORTH.ordinal()] = 3;
        arr[Direction.SOUTH.ordinal()] = 1;
        arr[Direction.WEST.ordinal()] = -1;
        arr[Direction.EAST.ordinal()] = -1;
    });
    private static final int[] DIR_ROT_Z_2D_DATA = Util.make(new int[6], arr -> {
        arr[Direction.DOWN.ordinal()] = 2;
        arr[Direction.UP.ordinal()] = 0;
        arr[Direction.NORTH.ordinal()] = -1;
        arr[Direction.SOUTH.ordinal()] = -1;
        arr[Direction.WEST.ordinal()] = 3;
        arr[Direction.EAST.ordinal()] = 1;
    });

    public static VoxelShape rotateShapeAroundY(Direction from, Direction to, VoxelShape shape) {
        return rotateShapeUnoptimizedAroundY(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundY(Direction from, Direction to, VoxelShape shape) {
        if (isY(from) || isY(to)) {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to) {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (AABB box : sourceBoxes) {
            for (int i = 0; i < times; i++)
            {
                box = new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static VoxelShape rotateShapeAroundX(Direction from, Direction to, VoxelShape shape) {
        return rotateShapeUnoptimizedAroundX(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundX(Direction from, Direction to, VoxelShape shape) {
        if (isX(from) || isX(to)) {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to)
        {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (DIR_ROT_X_2D_DATA[to.ordinal()] - DIR_ROT_X_2D_DATA[from.ordinal()] + 4) % 4;
        for (AABB box : sourceBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                box = new AABB(box.minX, 1 - box.maxZ, box.minY, box.maxX, 1 - box.minZ, box.maxY);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static VoxelShape rotateShapeAroundZ(Direction from, Direction to, VoxelShape shape) {
        return rotateShapeUnoptimizedAroundZ(from, to, shape).optimize();
    }

    public static VoxelShape rotateShapeUnoptimizedAroundZ(Direction from, Direction to, VoxelShape shape) {
        if (isZ(from) || isZ(to))
        {
            throw new IllegalArgumentException("Invalid Direction!");
        }
        if (from == to)
        {
            return shape;
        }

        List<AABB> sourceBoxes = shape.toAabbs();
        VoxelShape rotatedShape = Shapes.empty();
        int times = (DIR_ROT_Z_2D_DATA[to.ordinal()] - DIR_ROT_Z_2D_DATA[from.ordinal()] + 4) % 4;
        for (AABB box : sourceBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                //noinspection SuspiciousNameCombination
                box = new AABB(box.minY, 1 - box.maxX, box.minZ, box.maxY, 1 - box.minX, box.maxZ);
            }
            rotatedShape = orUnoptimized(rotatedShape, Shapes.create(box));
        }

        return rotatedShape;
    }

    public static boolean isX(Direction dir) {
        return dir.getAxis() == Direction.Axis.X;
    }

    public static boolean isY(Direction dir) {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isZ(Direction dir) {
        return dir.getAxis() == Direction.Axis.Z;
    }

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second) {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }

}
