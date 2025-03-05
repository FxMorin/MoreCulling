package ca.fxco.moreculling.utils;

import ca.fxco.moreculling.api.data.QuadBounds;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Math;
import org.joml.*;

public class VertexUtils {
    private static final int VERTEX_STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

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

    private static Vector3f getPos(int[] data, int vertexIndex) {
        final int index = vertexIndex * VERTEX_STRIDE;
        return new Vector3f(
                Float.intBitsToFloat(data[index]),
                Float.intBitsToFloat(data[index + 1]),
                Float.intBitsToFloat(data[index + 2])
        );
    }

    private static Vector2f getUv(TextureAtlasSprite sprite, int[] data, int vertexIndex, int imageWidth, int imageHeight) {
        final int index = vertexIndex * VERTEX_STRIDE;
        float width = sprite.getU1() - sprite.getU0();
        float height = sprite.getV1() - sprite.getV0();
        return new Vector2f(
                ((Float.intBitsToFloat(data[index + 4]) - sprite.getU0()) / width) * imageWidth, // x
                ((Float.intBitsToFloat(data[index + 5]) - sprite.getV0()) / height) * imageHeight // y
        );
    }

    public static QuadBounds getQuadUvBounds(BakedQuad quad, int imageWidth, int imageHeight) {
        Vector2i minPos = new Vector2i(Integer.MAX_VALUE);
        Vector2i maxPos = new Vector2i(-Integer.MAX_VALUE);
        int[] vertexData = quad.vertices();
        for (int i = 0; i < 4; i++) {
            Vector2f tmpPos = getUv(quad.sprite(), vertexData, i, imageWidth, imageHeight);
            Vector2i pos = new Vector2i(Math.round(tmpPos.x), Math.round(tmpPos.y));
            minPos.min(pos);
            maxPos.max(pos);
        }
        return new QuadBounds(minPos.x, minPos.y,
                maxPos.x, maxPos.y);
    }

    public static boolean isAxisAligned(BakedQuad quad) {
        int[] vertexData = quad.vertices();
        Vector3f p1 = getPos(vertexData, 0);
        Vector3f p4 = getPos(vertexData, 3);
        if (p1.y == p4.y) {
            Vector3f p2 = getPos(vertexData, 1);
            Vector3f p3 = getPos(vertexData, 2);
            return p2.y == p3.y && !(p1.y != p2.y && (p1.x != p3.x && p1.z != p3.z));
        }
        return false;
    }
}
