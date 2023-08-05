package ca.fxco.moreculling.utils;

import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

import static net.minecraft.util.math.Direction.*;

public class DirectionUtils {

    public static Direction changeDirectionUsingTransformation(Direction dir, Transformation transform) {
        if (transform.rotation.y() == 0) {
            return dir.getOpposite();
        } else if (transform.rotation.y() == 90) {
            return dir.rotateYCounterclockwise();
        } else if (transform.rotation.y() == 270) {
            return dir.rotateYClockwise();
        }
        return dir;
    }

    public static Direction magicalRotation(Direction face, int rotation) {
        return switch (rotation) {
            default -> face;
            case 90 -> face.rotateClockwise(Axis.Z);
            case 180 -> face != NORTH && face != SOUTH ? face.getOpposite() : face;
            case 270 -> face.rotateCounterclockwise(Axis.Z);
        };
    }

    public static Direction shiftDirection(Direction facing, Direction face, int rotation) {
        return switch (facing) {
            case DOWN -> switch (face) {
                case UP -> SOUTH;
                case DOWN -> NORTH;
                case SOUTH -> magicalRotation(UP, rotation);
                case NORTH -> magicalRotation(DOWN, rotation);
                default -> magicalRotation(face, rotation).getOpposite();
            };
            case UP -> switch (face) {
                case UP -> NORTH;
                case DOWN -> SOUTH;
                case SOUTH -> magicalRotation(DOWN, rotation);
                case NORTH -> magicalRotation(UP, rotation);
                default -> magicalRotation(face, rotation).getOpposite();
            };
            case NORTH -> magicalRotation(face, rotation);
            case SOUTH -> face != DOWN && face != UP ?
                    magicalRotation(face, rotation).getOpposite() :
                    magicalRotation(face, rotation);
            case WEST -> switch (face) {
                case EAST -> SOUTH;
                case WEST -> NORTH;
                case UP, DOWN -> magicalRotation(face, rotation);
                case SOUTH -> magicalRotation(WEST, rotation);
                case NORTH -> magicalRotation(EAST, rotation);
            };
            case EAST -> switch (face) {
                case EAST -> NORTH;
                case WEST -> SOUTH;
                case UP, DOWN -> magicalRotation(face, rotation);
                case SOUTH -> magicalRotation(EAST, rotation);
                case NORTH -> magicalRotation(WEST, rotation);
            };
        };
    }
}
