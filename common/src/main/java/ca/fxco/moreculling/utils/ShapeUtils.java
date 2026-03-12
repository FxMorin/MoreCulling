package ca.fxco.moreculling.utils;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.SymmetricGroup3;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.Util;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class ShapeUtils {

    public static final Map<Matrix4fc, OctahedralGroup> MATRIX_TO_OCTAHEDRAL = Util.make(new HashMap<>(), map -> {
        for (BlockModelRotation group : BlockModelRotation.values()) {
            if (group != BlockModelRotation.X0_Y0) {
                map.put(group.getRotation().getMatrix(), group.actualRotation());
            }
        }
    });

    private static final Vec3 BLOCK_CENTER = new Vec3(0.5, 0.5, 0.5);

    public static VoxelShape rotate(VoxelShape shape, OctahedralGroup octohedralGroup) {
        return rotate(shape, octohedralGroup, BLOCK_CENTER);
    }

    public static VoxelShape rotate(VoxelShape shape, OctahedralGroup octohedralGroup, Vec3 pos) {
        if (octohedralGroup == OctahedralGroup.IDENTITY) {
            return shape;
        } else {
            DiscreteVoxelShape discretevoxelshape = rotateDiscrete(shape.shape, octohedralGroup);
            if (shape instanceof CubeVoxelShape && BLOCK_CENTER.equals(pos)) {
                return new CubeVoxelShape(discretevoxelshape);
            } else {
                Direction.Axis direction$axis = permuteAxis(octohedralGroup.permutation, Direction.Axis.X);
                Direction.Axis direction$axis1 = permuteAxis(octohedralGroup.permutation, Direction.Axis.Y);
                Direction.Axis direction$axis2 = permuteAxis(octohedralGroup.permutation, Direction.Axis.Z);
                DoubleList doublelist = shape.getCoords(direction$axis);
                DoubleList doublelist1 = shape.getCoords(direction$axis1);
                DoubleList doublelist2 = shape.getCoords(direction$axis2);
                boolean flag = octohedralGroup.inverts(Direction.Axis.X);
                boolean flag1 = octohedralGroup.inverts(Direction.Axis.Y);
                boolean flag2 = octohedralGroup.inverts(Direction.Axis.Z);
                return new ArrayVoxelShape(
                        discretevoxelshape,
                        flipAxisIfNeeded(doublelist, flag, pos.get(direction$axis), pos.x),
                        flipAxisIfNeeded(doublelist1, flag1, pos.get(direction$axis1), pos.y),
                        flipAxisIfNeeded(doublelist2, flag2, pos.get(direction$axis2), pos.z)
                );
            }
        }
    }
    static DoubleList flipAxisIfNeeded(DoubleList p_455917_, boolean p_455550_, double p_455440_, double p_455918_) {
        if (!p_455550_ && p_455440_ == p_455918_) {
            return p_455917_;
        } else {
            int i = p_455917_.size();
            DoubleList doublelist = new DoubleArrayList(i);
            if (p_455550_) {
                for (int j = i - 1; j >= 0; j--) {
                    doublelist.add(-(p_455917_.getDouble(j) - p_455440_) + p_455918_);
                }
            } else {
                for (int k = 0; k >= 0 && k < i; k++) {
                    doublelist.add(p_455917_.getDouble(k) - p_455440_ + p_455918_);
                }
            }

            return doublelist;
        }
    }

    public static Direction.Axis permuteAxis(SymmetricGroup3 symmetricGroup3, Direction.Axis axis) {
        return Direction.Axis.VALUES[symmetricGroup3.permutation(axis.ordinal())];
    }


    public static DiscreteVoxelShape rotateDiscrete(DiscreteVoxelShape discrete, OctahedralGroup octahedralGroup) {
        if (octahedralGroup == OctahedralGroup.IDENTITY) {
            return discrete;
        } else {
            Vector3i vector3i = rotateOctahedral(octahedralGroup, new Vector3i(discrete.getXSize(), discrete.getYSize(), discrete.getZSize()));
            int i = fixupCoordinate(vector3i, 0);
            int j = fixupCoordinate(vector3i, 1);
            int k = fixupCoordinate(vector3i, 2);
            DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(vector3i.x, vector3i.y, vector3i.z);

            for (int l = 0; l < discrete.getXSize(); l++) {
                for (int i1 = 0; i1 < discrete.getYSize(); i1++) {
                    for (int j1 = 0; j1 < discrete.getZSize(); j1++) {
                        if (discrete.isFull(l, i1, j1)) {
                            Vector3i vector3i1 = rotateOctahedral(octahedralGroup, vector3i.set(l, i1, j1));
                            int k1 = i + vector3i1.x;
                            int l1 = j + vector3i1.y;
                            int i2 = k + vector3i1.z;
                            discretevoxelshape.fill(k1, l1, i2);
                        }
                    }
                }
            }

            return discretevoxelshape;
        }
    }

    private static int fixupCoordinate(Vector3i vector3i, int coordinate) {
        int i = vector3i.get(coordinate);
        if (i < 0) {
            vector3i.setComponent(coordinate, -i);
            return -i - 1;
        } else {
            return 0;
        }
    }


    public static Vector3i rotateOctahedral(OctahedralGroup permutation, Vector3i vector3i) {
        permuteVector(permutation.permutation, vector3i);
        vector3i.x = vector3i.x * (permutation.inverts(Direction.Axis.X) ? -1 : 1);
        vector3i.y = vector3i.y * (permutation.inverts(Direction.Axis.Y) ? -1 : 1);
        vector3i.z = vector3i.z * (permutation.inverts(Direction.Axis.Z) ? -1 : 1);
        return vector3i;
    }


    public static Vector3i permuteVector(SymmetricGroup3 group3, Vector3i vector3i) {
        int i = vector3i.get(group3.permutation(0));
        int j = vector3i.get(group3.permutation(1));
        int k = vector3i.get(group3.permutation(2));
        return vector3i.set(i, j, k);
    }

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second) {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }

}
