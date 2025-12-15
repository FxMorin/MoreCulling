package ca.fxco.moreculling.utils;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.AxisTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.*;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class ShapeUtils {
    private static final Vec3d BLOCK_CENTER = new Vec3d(0.5, 0.5, 0.5);

    public static final Map<Matrix4f, DirectionTransformation> MATRIX_TO_OCTAHEDRAL = Util.make(new HashMap<>(), map -> {
        for (ModelRotation group : ModelRotation.values()) {
            if (group != ModelRotation.X0_Y0) {
                map.put(group.getRotation().getMatrix(), group.getDirectionTransformation());
            }
        }
    });

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second) {
        return VoxelShapes.combine(first, second, BooleanBiFunction.OR);
    }


    public static VoxelShape rotate(VoxelShape shape, DirectionTransformation octohedralGroup) {
        return rotate(shape, octohedralGroup, BLOCK_CENTER);
    }


    public static VoxelShape rotate(VoxelShape shape, DirectionTransformation octohedralGroup, Vec3d pos) {
        if (octohedralGroup == DirectionTransformation.IDENTITY) {
            return shape;
        } else {
            VoxelSet discretevoxelshape = rotateVoxels(octohedralGroup, shape.voxels);
            if (shape instanceof SimpleVoxelShape && BLOCK_CENTER.equals(pos)) {
                return new SimpleVoxelShape(discretevoxelshape);
            } else {
                Direction.Axis direction$axis = Direction.Axis.VALUES[octohedralGroup.axisTransformation.map(Direction.Axis.X.ordinal())];
                Direction.Axis direction$axis1 = Direction.Axis.VALUES[octohedralGroup.axisTransformation.map(Direction.Axis.Y.ordinal())];
                Direction.Axis direction$axis2 = Direction.Axis.VALUES[octohedralGroup.axisTransformation.map(Direction.Axis.Z.ordinal())];
                DoubleList doublelist = shape.getPointPositions(direction$axis);
                DoubleList doublelist1 = shape.getPointPositions(direction$axis1);
                DoubleList doublelist2 = shape.getPointPositions(direction$axis2);
                boolean flag = octohedralGroup.shouldFlipDirection(Direction.Axis.X);
                boolean flag1 = octohedralGroup.shouldFlipDirection(Direction.Axis.Y);
                boolean flag2 = octohedralGroup.shouldFlipDirection(Direction.Axis.Z);
                return new ArrayVoxelShape(
                        discretevoxelshape,
                        flipAxisIfNeeded(doublelist, flag, pos.getComponentAlongAxis(direction$axis), pos.x),
                        flipAxisIfNeeded(doublelist1, flag1, pos.getComponentAlongAxis(direction$axis1), pos.y),
                        flipAxisIfNeeded(doublelist2, flag2, pos.getComponentAlongAxis(direction$axis2), pos.z)
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


    public static VoxelSet rotateVoxels(DirectionTransformation octahedralGroup, VoxelSet set) {
        if (octahedralGroup == DirectionTransformation.IDENTITY) {
            return set;
        } else {
            Vector3i vector3i = rotateVector3i(octahedralGroup, new Vector3i(set.getXSize(), set.getYSize(), set.getZSize()));
            int i = fixupCoordinate(vector3i, 0);
            int j = fixupCoordinate(vector3i, 1);
            int k = fixupCoordinate(vector3i, 2);
            VoxelSet discretevoxelshape = new BitSetVoxelSet(vector3i.x, vector3i.y, vector3i.z);

            for (int l = 0; l < set.getXSize(); l++) {
                for (int i1 = 0; i1 < set.getYSize(); i1++) {
                    for (int j1 = 0; j1 < set.getZSize(); j1++) {
                        if (set.contains(l, i1, j1)) {
                            Vector3i vector3i1 = rotateVector3i(octahedralGroup, vector3i.set(l, i1, j1));
                            int k1 = i + vector3i1.x;
                            int l1 = j + vector3i1.y;
                            int i2 = k + vector3i1.z;
                            discretevoxelshape.set(k1, l1, i2);
                        }
                    }
                }
            }

            return discretevoxelshape;
        }
    }


    public static Vector3i rotateVector3i(DirectionTransformation transformation, Vector3i p_454994_) {
        permuteVector(transformation.axisTransformation, p_454994_);
        p_454994_.x = p_454994_.x * (transformation.shouldFlipDirection(Direction.Axis.X) ? -1 : 1);
        p_454994_.y = p_454994_.y * (transformation.shouldFlipDirection(Direction.Axis.Y) ? -1 : 1);
        p_454994_.z = p_454994_.z * (transformation.shouldFlipDirection(Direction.Axis.Z) ? -1 : 1);
        return p_454994_;
    }


    public static Vector3i permuteVector(AxisTransformation transformation, Vector3i p_455667_) {
        int i = p_455667_.get(transformation.map(0));
        int j = p_455667_.get(transformation.map(1));
        int k = p_455667_.get(transformation.map(2));
        return p_455667_.set(i, j, k);
    }


    private static int fixupCoordinate(Vector3i p_454796_, int p_456115_) {
        int i = p_454796_.get(p_456115_);
        if (i < 0) {
            p_454796_.setComponent(p_456115_, -i);
            return -i - 1;
        } else {
            return 0;
        }
    }

}
