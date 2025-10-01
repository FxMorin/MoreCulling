package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.api.renderers.ExtendedBlockModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRenderer_cullMixin implements ExtendedBlockModelRenderer {

    @Unique
    private final List<BlockModelPart> moreculling$partList = new ArrayList<>();;

    @Unique
    private final RandomSource moreculling$rand = new SingleThreadedRandomSource(42L);

    @Shadow
    @Final
    private static Direction[] DIRECTIONS;

    @Override
    public void moreculling$renderQuad(PoseStack.Pose pose, VertexConsumer vertices, float red, float green,
                                       float blue, float alpha, BakedQuad bakedQuad, int light, int overlay) {
        if (bakedQuad.isTinted()) {
            vertices.putBulkData(
                    pose,
                    bakedQuad,
                    Mth.clamp(red, 0.0f, 1.0f),
                    Mth.clamp(green, 0.0f, 1.0f),
                    Mth.clamp(blue, 0.0f, 1.0f),
                    Mth.clamp(alpha, 0.0f, 1.0f),
                    light,
                    overlay
            );
        } else {
            vertices.putBulkData(pose, bakedQuad, 1.0f, 1.0f, 1.0f, 1.0f, light, overlay);
        }
    }

    @Override
    public void moreculling$renderModelWithoutFace(PoseStack.Pose pose, VertexConsumer vertices,
                                                   @Nullable BlockState state, BlockStateModel model, float red,
                                                   float green, float blue, float alpha, int light,
                                                   int overlay, Direction withoutFace) {
        moreculling$partList.clear();
        this.moreculling$rand.setSeed(42L);
        model.collectParts(this.moreculling$rand, moreculling$partList);
        for (BlockModelPart part : moreculling$partList) {
            for (Direction direction : DIRECTIONS) {
                if (direction == withoutFace) {
                    continue;
                }
                List<BakedQuad> bakedQuads = part.getQuads(direction);
                if (!bakedQuads.isEmpty()) {
                    moreculling$renderQuads(pose, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
                }
            }
            List<BakedQuad> bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsWithoutFace(pose, vertices, red, green, blue, alpha,
                        bakedQuads, light, overlay, withoutFace);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsWithoutFace(PoseStack.Pose pose, VertexConsumer vertices,
                                                   float red, float green, float blue, float alpha,
                                                   List<BakedQuad> quads, int light, int overlay,
                                                   Direction withoutFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.direction() == withoutFace) {
                continue;
            }
            moreculling$renderQuad(pose, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
        }
    }

    @Override
    public void moreculling$renderModelForFace(PoseStack.Pose pose, VertexConsumer vertices,
                                               @Nullable BlockState state, BlockStateModel model, float red, float green,
                                               float blue, float alpha, int light, int overlay, Direction forFace) {
        moreculling$partList.clear();
        this.moreculling$rand.setSeed(42L);
        model.collectParts(this.moreculling$rand, moreculling$partList);
        for (BlockModelPart part : moreculling$partList) {
            List<BakedQuad> bakedQuads = part.getQuads(forFace);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
            }
            bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsForFace(pose, vertices, red, green, blue, alpha,
                        bakedQuads, light, overlay, forFace);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsForFace(PoseStack.Pose pose, VertexConsumer vertices, float red,
                                               float green, float blue, float alpha, List<BakedQuad> quads, int light,
                                               int overlay, Direction forFace) {
        for (BakedQuad bakedQuad : quads) {
            if (bakedQuad.direction() != forFace) {
                continue;
            }
            moreculling$renderQuad(pose, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
        }
    }

    @Override
    public void moreculling$renderModelFor3Faces(PoseStack.Pose pose, VertexConsumer vertices,
                                                 @Nullable BlockState state, BlockStateModel model, float red, float green,
                                                 float blue, float alpha, int light, int overlay, Direction faceX,
                                                 Direction faceY, Direction faceZ) {
        moreculling$partList.clear();
        this.moreculling$rand.setSeed(42L);
        model.collectParts(this.moreculling$rand, moreculling$partList);
        for (BlockModelPart part : moreculling$partList) {
            List<BakedQuad> bakedQuads = part.getQuads(faceX);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
            }
            bakedQuads = part.getQuads(faceY);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
            }
            bakedQuads = part.getQuads(faceZ);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuads(pose, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
            }
            bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsFor3Faces(pose, vertices, red, green, blue, alpha,
                        bakedQuads, light, overlay, faceX, faceY, faceZ);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsFor3Faces(PoseStack.Pose pose, VertexConsumer vertices, float red,
                                                 float green, float blue, float alpha, List<BakedQuad> quads,
                                                 int light, int overlay, Direction faceX, Direction faceY,
                                                 Direction faceZ) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.direction();
            if (face == faceX || face == faceY || face == faceZ) {
                moreculling$renderQuad(pose, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
            }
        }
    }

    @Override
    public void moreculling$renderModelForFaces(PoseStack.Pose pose, VertexConsumer vertices,
                                                @Nullable BlockState state, BlockStateModel model, float red, float green,
                                                float blue, float alpha, int light, int overlay, Direction[] faces) {
        moreculling$partList.clear();
        this.moreculling$rand.setSeed(42L);
        model.collectParts(this.moreculling$rand, moreculling$partList);
        for (BlockModelPart part : moreculling$partList) {
            for (Direction direction : faces) {
                List<BakedQuad> bakedQuads = part.getQuads(direction);
                if (!bakedQuads.isEmpty()) {
                    moreculling$renderQuads(pose, vertices, red, green, blue, alpha, bakedQuads, light, overlay);
                }
            }

            List<BakedQuad> bakedQuads = part.getQuads(null);
            if (!bakedQuads.isEmpty()) {
                moreculling$renderQuadsForFaces(pose, vertices, red, green, blue, alpha,
                        bakedQuads, light, overlay, faces);
            }
        }
    }

    @Override
    public void moreculling$renderQuadsForFaces(PoseStack.Pose pose, VertexConsumer vertices, float red,
                                                float green, float blue, float alpha, List<BakedQuad> quads,
                                                int light, int overlay, Direction[] faces) {
        for (BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.direction();
            if (Arrays.stream(faces).anyMatch((f) -> f == face)) {
                moreculling$renderQuad(pose, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
            }
        }
    }

    @Unique
    private void moreculling$renderQuads(PoseStack.Pose pose, VertexConsumer vertices, float red, float green,
                                         float blue, float alpha, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            moreculling$renderQuad(pose, vertices, red, green, blue, alpha, bakedQuad, light, overlay);
        }
    }
}
