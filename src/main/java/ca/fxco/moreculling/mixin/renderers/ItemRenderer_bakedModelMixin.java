package ca.fxco.moreculling.mixin.renderers;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.api.renderers.ExtendedItemRenderer;
import ca.fxco.moreculling.utils.CacheUtils;
import ca.fxco.moreculling.utils.CullingUtils;
import ca.fxco.moreculling.utils.DirectionUtils;
import ca.fxco.moreculling.utils.TransformationUtils;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

import static ca.fxco.moreculling.utils.DirectionUtils.shiftDirection;
import static net.minecraft.client.render.item.ItemRenderer.*;
import static net.minecraft.util.math.Direction.*;

@Mixin(ItemRenderer.class)
public abstract class ItemRenderer_bakedModelMixin implements ExtendedItemRenderer {

    @Unique
    private final Random rand = Random.create(42L);

    @Shadow
    @Final
    private ItemModels models;

    @Shadow
    @Final
    private BuiltinModelItemRenderer builtinModelItemRenderer;

    @Shadow
    protected abstract void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                                 ItemStack stack, int light, int overlay);

    @Shadow
    @Final
    private ItemColors colors;

    @Override
    public final ThreadLocal<Object2IntLinkedOpenHashMap<BakedQuad>> getBakedQuadColorCache() {
        return CacheUtils.BAKED_QUAD_COLOR_CACHE;
    }

    @Override
    public BakedModel customGetModel(ItemStack stack, int seed) {
        BakedModel bakedModel = this.models.getModel(stack);
        BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, null, null, seed);
        return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
    }

    @Override
    public void renderBakedItemQuad(VertexConsumer vertices, ItemStack stack, int light, int overlay,
                                    MatrixStack.Entry entry, BakedQuad bakedQuad) {
        int color;
        if (bakedQuad.hasColor()) {
            Object2IntLinkedOpenHashMap<BakedQuad> bakedQuadColorCache = CacheUtils.BAKED_QUAD_COLOR_CACHE.get();
            color = bakedQuadColorCache.getAndMoveToFirst(bakedQuad);
            if (color == Integer.MAX_VALUE) {
                color = this.colors.getColor(stack, bakedQuad.getColorIndex());
                if (bakedQuadColorCache.size() == 256) bakedQuadColorCache.removeLastInt();
            }
        } else {
            color = -1;
        }
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        vertices.quad(entry, bakedQuad, r, g, b, light, overlay);
    }


    @Override
    public void renderBakedItemQuadsWithoutFace(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                                ItemStack stack, int light, int overlay, Direction withoutFace) {
        MatrixStack.Entry entry = matrices.peek();
        for(BakedQuad bakedQuad : quads) {
            if (bakedQuad.getFace() == withoutFace) continue;
            renderBakedItemQuad(vertices, stack, light, overlay, entry, bakedQuad);
        }
    }

    @Override
    public void renderBakedItemQuadsForFace(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                            ItemStack stack, int light, int overlay, Direction face) {
        MatrixStack.Entry entry = matrices.peek();
        for(BakedQuad bakedQuad : quads) {
            if (bakedQuad.getFace() != face) continue;
            renderBakedItemQuad(vertices, stack, light, overlay, entry, bakedQuad);
        }
    }

    @Override
    public void renderBakedItemQuadsFor3Faces(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads,
                                              ItemStack stack, int light, int overlay,
                                              Direction faceX, Direction faceY, Direction faceZ) {
        MatrixStack.Entry entry = matrices.peek();
        for(BakedQuad bakedQuad : quads) {
            Direction face = bakedQuad.getFace();
            if (face == faceX || face == faceY || face == faceZ)
                renderBakedItemQuad(vertices, stack, light, overlay, entry, bakedQuad);
        }
    }


    @Override
    public void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                 MatrixStack matrices, VertexConsumer vertices,
                                                @Nullable Direction withoutFace) {
        for(Direction direction : Direction.values()) {
            if (direction == withoutFace) continue;
            rand.setSeed(42L);
            List<BakedQuad> bakedQuads = model.getQuads(null, direction, rand);
            if (!bakedQuads.isEmpty()) // TODO: should I render without a face here also
                this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        }
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(null, null, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuadsWithoutFace(matrices, vertices, bakedQuads, stack, light, overlay, withoutFace);
    }

    @Override
    public void renderBakedItemModelOnly3Faces(BakedModel model, ItemStack stack, int light, int overlay,
                                               MatrixStack matrices, VertexConsumer vertices,
                                               Direction faceX, Direction faceY, Direction faceZ) {
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(null, faceX, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(null, faceY, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(null, faceZ, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(null, null, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuadsFor3Faces(
                    matrices, vertices, bakedQuads, stack, light, overlay, faceX, faceY, faceZ
            );
    }

    @Override
    public void renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                MatrixStack matrices, VertexConsumer vertices,
                                                Direction face) {
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(null, face, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        rand.setSeed(42L);
        bakedQuads = model.getQuads(null, null, rand);
        if (!bakedQuads.isEmpty())
            this.renderBakedItemQuadsForFace(matrices, vertices, bakedQuads, stack, light, overlay, face);
    }

    @Override
    public void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vc,
                                    int light, int seed, ItemFrameEntity frame, Vec3d cameraPos) {
        BakedModel model = this.customGetModel(stack, seed);
        matrices.push();
        ModelTransformationMode fixedMode = ModelTransformationMode.FIXED;
        Transformation transformation = model.getTransformation().getTransformation(fixedMode);
        transformation.apply(false, matrices);
        matrices.translate(-0.5, -0.5, -0.5);
        if (model.isBuiltin()) {
            this.builtinModelItemRenderer.render(stack, fixedMode, matrices, vc, light, OverlayTexture.DEFAULT_UV);
        } else {
            boolean isBlockItem = stack.getItem() instanceof BlockItem; //TODO: Do proper checks
            // Use faster cached check for translucency instead of multiple instanceof checks
            boolean bl2 = !isBlockItem || !((BakedOpacity) model).hasTextureTranslucency(
                    ((BlockItem) stack.getItem()).getBlock().getDefaultState()
            );
            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
            VertexConsumer vertexConsumer;
            if (stack.isIn(ItemTags.COMPASSES) && stack.hasGlint()) {
                matrices.push();
                MatrixStack.Entry entry = matrices.peek();
                vertexConsumer = bl2 ? getDirectDynamicDisplayGlintConsumer(vc, renderLayer, entry) :
                        getDynamicDisplayGlintConsumer(vc, renderLayer, entry);
                matrices.pop();
            } else {
                vertexConsumer = bl2 ? getDirectItemGlintConsumer(vc, renderLayer, true, stack.hasGlint()) :
                        getItemGlintConsumer(vc, renderLayer, true, stack.hasGlint());
            }
            Vec3d framePos = frame.getPos();
            double dist = cameraPos.squaredDistanceTo(framePos);
            boolean canCull = ((!isBlockItem && !frame.isInvisible()) || CullingUtils.shouldCullBack(frame)) &&
                    TransformationUtils.canCullTransformation(transformation);
            // Make blocks use LOD - If more than range, only render the front and maybe back if can't cull
            if (MoreCulling.CONFIG.useItemFrameLOD && !isBlockItem && dist > MoreCulling.CONFIG.itemFrameLODRange) {
                this.renderBakedItemModelForFace(
                        model, stack, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumer, SOUTH
                );
                if (!canCull) { // Render back if it can't cull the back
                    this.renderBakedItemModelForFace(
                            model, stack, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumer, NORTH
                    );
                }
            } else {
                // EXPERIMENTAL CULLING
                // Use smart culling to render only 3 face directions.
                // TODO: Add model rotation logic (items need this!) Currently we only support blocks and some models
                if (MoreCulling.CONFIG.useItemFrame3FaceCulling &&
                        dist > MoreCulling.CONFIG.itemFrame3FaceCullingRange &&
                        frame.getRotation() % 2 == 0 &&
                        transformation.rotation.y() == 0 &&
                        transformation.rotation.x() == 0 &&
                        transformation.rotation.z() == 0
                ) {
                    int rotation = frame.getRotation() * 45;
                    Direction facing = frame.getHorizontalFacing();
                    Direction dirX = cameraPos.x > framePos.x ?
                            shiftDirection(facing, Direction.EAST, rotation) :
                            shiftDirection(facing, Direction.WEST, rotation);
                    Direction dirY = cameraPos.y > framePos.y ?
                            shiftDirection(facing, Direction.UP, rotation) :
                            shiftDirection(facing, Direction.DOWN, rotation);
                    Direction dirZ = cameraPos.z > framePos.z ?
                            shiftDirection(facing, Direction.SOUTH, rotation) :
                            shiftDirection(facing, Direction.NORTH, rotation);
                    this.renderBakedItemModelOnly3Faces(
                            model,
                            stack,
                            light,
                            OverlayTexture.DEFAULT_UV,
                            matrices,
                            vertexConsumer,
                            dirX,
                            dirY,
                            dirZ
                    );
                } else {
                    this.renderBakedItemModelWithoutFace(
                            model,
                            stack,
                            light,
                            OverlayTexture.DEFAULT_UV,
                            matrices,
                            vertexConsumer,
                            canCull ? DirectionUtils.changeDirectionUsingTransformation(
                                    Direction.NORTH,
                                    transformation
                            ) : null
                    );
                }
            }
        }
        matrices.pop();
    }
}
