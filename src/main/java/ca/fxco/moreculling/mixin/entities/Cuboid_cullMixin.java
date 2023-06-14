package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.VertexUtils;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.Cuboid.class)
public class Cuboid_cullMixin {

    @Shadow @Final private ModelPart.Quad[] sides;

    @Inject(
            method = "renderCuboid",
            at = @At("HEAD"),
            cancellable = true
    )
    public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay,
                             float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.entityModelCulling) {
            return;
        }
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        for (ModelPart.Quad quad : this.sides) {
            ModelPart.Vertex[] vertices = quad.vertices;

            if (VertexUtils.isTriangleInvisible(
                    VertexUtils.applyTransformation(vertices[0].pos, matrix4f),
                    VertexUtils.applyTransformation(vertices[1].pos, matrix4f),
                    VertexUtils.applyTransformation(vertices[2].pos, matrix4f)
            )) {
                continue;
            }

            Vector3f vector3f = matrix3f.transform(new Vector3f(quad.direction));
            float nx = vector3f.x(), ny = vector3f.y(), nz = vector3f.z();

            for (ModelPart.Vertex vertex : vertices) {
                Vector4f vector4f = matrix4f.transform(new Vector4f(
                        vertex.pos.x() * VertexUtils.FAST_NORM,
                        vertex.pos.y() * VertexUtils.FAST_NORM,
                        vertex.pos.z() * VertexUtils.FAST_NORM,
                        1.0F
                ));
                vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(),
                        red, green, blue, alpha, vertex.u, vertex.v, overlay, light, nx, ny, nz);
            }
        }
        ci.cancel();
    }
}
