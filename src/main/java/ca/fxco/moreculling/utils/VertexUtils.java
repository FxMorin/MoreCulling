package ca.fxco.moreculling.utils;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class VertexUtils {

    public static final float FAST_NORM = 0.0625F; // 1 / 16

    public static boolean isTriangleInvisible(Vector3f p0, Vector3f p1, Vector3f p2) {
        return p1.sub(p0).cross(p2.sub(p0)).dot(p0) >= 0;
    }

    public static Vector3f applyTransformation(Vector3f vector, Matrix4f matrix) {
        float x2 = vector.x() * FAST_NORM;
        float y2 = vector.y() * FAST_NORM;
        float z2 = vector.z() * FAST_NORM;
        return new Vector3f(
                Math.fma(matrix.m00(), x2, Math.fma(matrix.m10(), y2, Math.fma(matrix.m20(), z2, matrix.m30()))),
                Math.fma(matrix.m01(), x2, Math.fma(matrix.m11(), y2, Math.fma(matrix.m21(), z2, matrix.m31()))),
                Math.fma(matrix.m02(), x2, Math.fma(matrix.m12(), y2, Math.fma(matrix.m22(), z2, matrix.m32())))
        );
    }
}
