package ca.fxco.moreculling.utils;

import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.Direction;

import static net.minecraft.util.math.Direction.*;

public class DirectionUtils {

    public static Direction changeDirectionUsingTransformation(Direction dir, Transformation transform) {
        if (transform.rotation.getY() == 0) {
            return dir.getOpposite();
        } else if (transform.rotation.getY() == 90) {
            return dir.rotateYCounterclockwise();
        } else if (transform.rotation.getY() == 270) {
            return dir.rotateYClockwise();
        }
        return dir;
    }

    public static Direction transformDirectionByRotationY(Direction dir, int rotation) {
        return switch (rotation) {
            case 270 -> dir.rotateYCounterclockwise();
            case 180 -> dir.getOpposite();
            case 90 -> dir.rotateYClockwise();
            default -> dir;
        };
    }

    public static Direction transformDirectionByRotationX(Direction dir, int rotation) {
        return switch (rotation) {
            case 270 -> dir.rotateCounterclockwise(Axis.X);
            case 180 -> dir.getOpposite();
            case 90 -> dir.rotateClockwise(Axis.X);
            default -> dir;
        };
    }

    public static Direction transformDirectionByRotationZ(Direction dir, int rotation) {
        return switch (rotation) {
            case 270 -> dir.rotateCounterclockwise(Axis.Z);
            case 180 -> dir.getOpposite();
            case 90 -> dir.rotateClockwise(Axis.Z);
            default -> dir;
        };
    }

    public static Direction getDirectionFromFacing(Direction dir, Direction facing) {
        return switch(facing) {
            case DOWN -> dir.rotateCounterclockwise(Axis.X);
            case UP -> dir.rotateClockwise(Axis.X);
            case NORTH -> dir != UP && dir != DOWN ? oppositeY(dir) : dir;
            case SOUTH -> dir;
            case WEST -> dir != UP && dir != DOWN ? dir.rotateYCounterclockwise() : dir;
            case EAST -> dir != UP && dir != DOWN ? dir.rotateYClockwise() : dir;
        };
    }

    public static Direction oppositeY(Direction dir) {
        return switch(dir) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
            default -> throw new IllegalStateException("Unable to get OY facing of " + dir);
        };
    }

    public static Direction rotateYOrdinalTop(Direction dir) {
        return switch(dir) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get PL facing of " + dir);
        };
    }

    public static Direction rotateYOrdinalBottom(Direction dir) {
        return switch(dir) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
            default -> throw new IllegalStateException("Unable to get PR facing of " + dir);
        };
    }
}
