package ca.fxco.moreculling.utils;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.core.Direction;

import java.util.Map;

import static net.minecraft.core.Direction.*;

public class DirectionUtils {

    public static final Direction[] DIRECTIONS = Direction.values();

    private static final Map<Direction, Direction[]> DIRECTIONS_EXCLUDING_DIRECTION_MAP = Map.of( // Cached
            DOWN, new Direction[] { UP, NORTH, SOUTH, WEST, EAST },
            UP, new Direction[] { DOWN, NORTH, SOUTH, WEST, EAST },
            NORTH, new Direction[] { DOWN, UP, SOUTH, WEST, EAST },
            SOUTH, new Direction[] { DOWN, UP, NORTH, WEST, EAST },
            WEST, new Direction[] { DOWN, UP, NORTH, SOUTH, EAST },
            EAST, new Direction[] { DOWN, UP, NORTH, SOUTH, WEST }
    );

    public static Direction[] getAllDirectionsExcluding(Direction excluding) {
        return DIRECTIONS_EXCLUDING_DIRECTION_MAP.get(excluding);
    }

    public static Direction changeDirectionUsingTransformation(Direction dir, ItemTransform transform) {
        float yRot = transform.rotation().y();
        if (yRot == 0) {
            return dir.getOpposite();
        } else if (yRot == 90) {
            return dir.getCounterClockWise();
        } else if (yRot == 270) {
            return dir.getClockWise();
        }
        return dir;
    }

    public static Direction magicalRotation(Direction face, int rotation) {
        return switch (rotation) {
            default -> face;
            case 90 -> face.getClockWise(Axis.Z);
            case 180 -> face != NORTH && face != SOUTH ? face.getOpposite() : face;
            case 270 -> face.getCounterClockWise(Axis.Z);
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
