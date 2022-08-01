package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;

@Mixin(BlockModelRenderer.class)
public class BlockModelRenderer_cullMixin implements ExtendedBlockModelRenderer {

    @Unique
    private final Random rand = Random.create(42L);

    @Shadow
    @Final
    private static Direction[] DIRECTIONS;

    @Override
    public void renderQuad(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green, float blue,
                           BakedQuad bakedQuad, int light, int overlay) {
        if (bakedQuad.hasColor()) {
            vertices.quad(
                    entry,
                    bakedQuad,
                    MathHelper.clamp(red, 0.0f, 1.0f),
                    MathHelper.clamp(green, 0.0f, 1.0f),
                    MathHelper.clamp(blue, 0.0f, 1.0f),
                    light,
                    overlay
            );
        } else {
            vertices.quad(entry, bakedQuad, 1.0f, 1.0f, 1.0f, light, overlay);
        }
    }

    @Override
    public void renderModelWithoutFace(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                                       BakedModel model, float red, float green, float blue,
                                       int light, int overlay, Direction withoutFace) {
        for (Direction direction : DIRECTIONS) {
            if (direction == withoutFace) continue;
            rand.setSeed(42L);
            List<BakedQuad> bakedQuads = model.getQuads(state, direction, rand);
            if (!bakedQuads.isEmpty())
                renderQuads(entry, vertices, red, green, blue, bakedQuads, light, overlay);
        }
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, null, rand);
        if (!bakedQuads.isEmpty())
            renderQuadsWithoutFace(entry, vertices, red, green, blue, bakedQuads, light, overlay, withoutFace);
    }

    @Override
    public void renderQuadsWithoutFace(MatrixStack.Entry entry, VertexConsumer vertices,
                                       float red, float green, float blue, List<BakedQuad> quads,
                                       int light, int overlay, Direction withoutFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.getFace() == withoutFace) continue;
            renderQuad(entry, vertices, red, green, blue, bakedQuad, light, overlay);
        }
    }

    @Override
    public void renderModelForFace(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                                   BakedModel model, float red, float green, float blue,
                                   int light, int overlay, Direction forFace) {
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, forFace, rand);
            if (!bakedQuads.isEmpty())
                renderQuads(entry, vertices, red, green, blue, bakedQuads, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(state, null, rand);
        if (!bakedQuads.isEmpty())
            renderQuadsForFace(entry, vertices, red, green, blue, bakedQuads, light, overlay, forFace);
    }

    @Override
    public void renderQuadsForFace(MatrixStack.Entry entry, VertexConsumer vertices,
                                   float red, float green, float blue, List<BakedQuad> quads,
                                   int light, int overlay, Direction forFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.getFace() != forFace) continue;
            renderQuad(entry, vertices, red, green, blue, bakedQuad, light, overlay);
        }
    }

    @Override
    public void renderModelFor3Faces(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                                     BakedModel model, float red, float green, float blue,
                                     int light, int overlay, Direction faceX, Direction faceY, Direction faceZ) {
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, faceX, rand);
        if (!bakedQuads.isEmpty())
            renderQuads(entry, vertices, red, green, blue, bakedQuads, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(state, faceY, rand);
        if (!bakedQuads.isEmpty())
            renderQuads(entry, vertices, red, green, blue, bakedQuads, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(state, faceZ, rand);
        if (!bakedQuads.isEmpty())
            renderQuads(entry, vertices, red, green, blue, bakedQuads, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(state, null, rand);
        if (!bakedQuads.isEmpty())
            renderQuadsFor3Faces(entry, vertices, red, green, blue, bakedQuads, light, overlay, faceX, faceY, faceZ);
    }

    @Override
    public void renderQuadsFor3Faces(MatrixStack.Entry entry, VertexConsumer vertices,
                                     float red, float green, float blue, List<BakedQuad> quads, int light, int overlay,
                                     Direction faceX, Direction faceY, Direction faceZ) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.getFace();
            if (face == faceX || face == faceY || face == faceZ)
                renderQuad(entry, vertices, red, green, blue, bakedQuad, light, overlay);
        }
    }

    @Override
    public void renderModelForFaces(MatrixStack.Entry entry, VertexConsumer vertices, @Nullable BlockState state,
                                    BakedModel model, float red, float green, float blue,
                                    int light, int overlay, Direction[] faces) {
        for (Direction direction : faces) {
            rand.setSeed(42L);
            List<BakedQuad> bakedQuads = model.getQuads(state, direction, rand);
            if (!bakedQuads.isEmpty())
                renderQuads(entry, vertices, red, green, blue, bakedQuads, light, overlay);
        }
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, null, rand);
        if (!bakedQuads.isEmpty())
            renderQuadsForFaces(entry, vertices, red, green, blue, bakedQuads, light, overlay, faces);
    }

    @Override
    public void renderQuadsForFaces(MatrixStack.Entry entry, VertexConsumer vertices,
                                    float red, float green, float blue, List<BakedQuad> quads,
                                    int light, int overlay, Direction[] faces) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.getFace();
            if (Arrays.stream(faces).anyMatch((f) -> f == face))
                renderQuad(entry, vertices, red, green, blue, bakedQuad, light, overlay);
        }
    }

    private void renderQuads(MatrixStack.Entry entry, VertexConsumer vertices,
                           float red, float green, float blue, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedQuad : quads)
            renderQuad(entry, vertices, red, green, blue, bakedQuad, light, overlay);
    }
}
