package ca.fxco.moreculling.mixin.entities;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.VertexUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
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

@Mixin(ModelPart.Cube.class)
public class Cube_cullMixin {

    @Shadow
    @Final
    private ModelPart.Polygon[] polygons;

    @Inject(
            method = "compile",
            at = @At("HEAD"),
            cancellable = true
    )
    private void moreculling$renderCuboid(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, int j, int k, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.entityModelCulling) {
            return;
        }
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        for (ModelPart.Polygon quad : this.polygons) {
            ModelPart.Vertex[] vertices = quad.vertices;

            if (VertexUtils.isTriangleInvisible(
                    VertexUtils.applyTransformation(vertices[0].pos, matrix4f),
                    VertexUtils.applyTransformation(vertices[1].pos, matrix4f),
                    VertexUtils.applyTransformation(vertices[2].pos, matrix4f)
            )) {
                continue;
            }

            Vector3f vector3f = matrix3f.transform(new Vector3f(quad.normal));
            float nx = vector3f.x(), ny = vector3f.y(), nz = vector3f.z();

            for (ModelPart.Vertex vertex : vertices) {
                Vector4f vector4f = matrix4f.transform(new Vector4f(
                        vertex.pos.x() * VertexUtils.FAST_NORM,
                        vertex.pos.y() * VertexUtils.FAST_NORM,
                        vertex.pos.z() * VertexUtils.FAST_NORM,
                        1.0F
                ));
                vertexConsumer.addVertex(vector4f.x(), vector4f.y(), vector4f.z());
            }
        }
        ci.cancel();
    }
}
