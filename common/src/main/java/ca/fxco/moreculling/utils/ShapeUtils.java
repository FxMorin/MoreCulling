package ca.fxco.moreculling.utils;

import com.mojang.math.OctahedralGroup;
import net.minecraft.Util;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.HashMap;
import java.util.Map;

public class ShapeUtils {

    public static final Map<Matrix4fc, OctahedralGroup> MATRIX_TO_OCTAHEDRAL = Util.make(new HashMap<>(), map -> {
        for (OctahedralGroup group : OctahedralGroup.values()) {
            if (group != OctahedralGroup.IDENTITY) {
                map.put(new Matrix4f(group.transformation()), group);
            }
        }
    });

    public static VoxelShape orUnoptimized(VoxelShape first, VoxelShape second) {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }

}
