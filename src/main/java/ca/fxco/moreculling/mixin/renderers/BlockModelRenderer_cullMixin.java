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
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRenderer_cullMixin implements ExtendedBlockModelRenderer {

    @Unique
    private final Random moreculling$rand = new LocalRandom(42L);

    @Shadow
    @Final
    private static Direction[] DIRECTIONS;

    @Override
    public void moreculling$renderQuad(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green,
                                       float blue, float alpha, BakedQuad bakedQuad, int light, int overlay) {
        if (bakedQuad.hasColor()) {
            vertices.quad(
                    entry,
                    bakedQuad,
                    MathHelper.clamp(red, 0.0f, 1.0f),
                    MathHelper.clamp(green, 0.0f, 1.0f),
                    MathHelper.clamp(blue, 0.0f, 1.0f),
                    MathHelper.clamp(alpha, 0.0f, 1.0f),
                    light,
                    overlay
            );
        } else {
            vertices.quad(entry, bakedQuad, 1.0f, 1.0f, 1.0f, 1.0f, light, overlay);
        }
    }

    @Override
    public void moreculling$renderModelWithoutFace(MatrixStack.Entry entry, VertexConsumer vertices,
                                                   @Nullable BlockState state, BakedModel model, float red,
                                                   float green, float blue, float alpha, int light,
                                                   int overlay, Direction withoutFace) {
        for (Direction direction : DIRECTIONS) {
            if (direction == withoutFace) {
                continue;
            }
            this.moreculling$rand.setSeed(42L);
            List<BakedQuad> bakedQuads = model.getQuads(state, direction, this.moreculling$rand);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(entry, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
            }
        }
        this.moreculling$rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, null, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuadsWithoutFace(entry, vertices, red, green, blue, alpha,
                    bakedQuads, light, overlay, withoutFace);
        }
    }

    @Override
    public void moreculling$renderQuadsWithoutFace(MatrixStack.Entry entry, VertexConsumer vertices,
                                                   float red, float green, float blue, float alpha,
                                                   List<BakedQuad> quads, int light, int overlay,
                                                   Direction withoutFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.getFace() == withoutFace) {
                continue;
            }
            moreculling$renderQuad(entry, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
        }
    }

    @Override
    public void moreculling$renderModelForFace(MatrixStack.Entry entry, VertexConsumer vertices,
                                               @Nullable BlockState state, BakedModel model, float red, float green,
                                               float blue, float alpha, int light, int overlay, Direction forFace) {
        this.moreculling$rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, forFace, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuads(entry, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
        }
        this.moreculling$rand.setSeed(42L);
        bakedQuads = model.getQuads(state, null, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuadsForFace(entry, vertices, red, green, blue, alpha,
                    bakedQuads, light, overlay, forFace);
        }
    }

    @Override
    public void moreculling$renderQuadsForFace(MatrixStack.Entry entry, VertexConsumer vertices, float red,
                                               float green, float blue, float alpha, List<BakedQuad> quads, int light,
                                               int overlay, Direction forFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.getFace() != forFace) {
                continue;
            }
            moreculling$renderQuad(entry, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
        }
    }

    @Override
    public void moreculling$renderModelFor3Faces(MatrixStack.Entry entry, VertexConsumer vertices,
                                                 @Nullable BlockState state, BakedModel model, float red, float green,
                                                 float blue, float alpha, int light, int overlay, Direction faceX,
                                                 Direction faceY, Direction faceZ) {
        this.moreculling$rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, faceX, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuads(entry, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
        }
        this.moreculling$rand.setSeed(42L);
        bakedQuads = model.getQuads(state, faceY, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuads(entry, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
        }
        this.moreculling$rand.setSeed(42L);
        bakedQuads = model.getQuads(state, faceZ, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuads(entry, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
        }
        this.moreculling$rand.setSeed(42L);
        bakedQuads = model.getQuads(state, null, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuadsFor3Faces(entry, vertices, red, green, blue, alpha,
                    bakedQuads, light, overlay, faceX, faceY, faceZ);
        }
    }

    @Override
    public void moreculling$renderQuadsFor3Faces(MatrixStack.Entry entry, VertexConsumer vertices, float red,
                                                 float green, float blue, float alpha, List<BakedQuad> quads,
                                                 int light, int overlay, Direction faceX, Direction faceY,
                                                 Direction faceZ) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.getFace();
            if (face == faceX || face == faceY || face == faceZ) {
                moreculling$renderQuad(entry, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
            }
        }
    }

    @Override
    public void moreculling$renderModelForFaces(MatrixStack.Entry entry, VertexConsumer vertices,
                                                @Nullable BlockState state, BakedModel model, float red, float green,
                                                float blue, float alpha, int light, int overlay, Direction[] faces) {
        for (Direction direction : faces) {
            this.moreculling$rand.setSeed(42L);
            List<BakedQuad> bakedQuads = model.getQuads(state, direction, this.moreculling$rand);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(entry, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
            }
        }
        this.moreculling$rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(state, null, this.moreculling$rand);
        if (!bakedQuads.isEmpty()) {
            moreculling$renderQuadsForFaces(entry, vertices, red, green, blue, alpha,
                    bakedQuads, light, overlay, faces);
        }
    }

    @Override
    public void moreculling$renderQuadsForFaces(MatrixStack.Entry entry, VertexConsumer vertices, float red,
                                                float green, float blue, float alpha, List<BakedQuad> quads,
                                                int light, int overlay, Direction[] faces) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.getFace();
            if (Arrays.stream(faces).anyMatch((f) -> f == face)) {
                moreculling$renderQuad(entry, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
            }
        }
    }

    @Unique
    private void moreculling$renderQuads(MatrixStack.Entry entry, VertexConsumer vertices, float red, float green,
                                         float blue, float alpha, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            moreculling$renderQuad(entry, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
        }
    }
}
