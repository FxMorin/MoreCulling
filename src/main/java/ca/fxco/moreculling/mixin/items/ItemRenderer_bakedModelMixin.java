package ca.fxco.moreculling.mixin.items;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.patches.ExtendedItemRenderer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

import static net.minecraft.client.render.item.ItemRenderer.*;

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
    public abstract BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

    @Shadow
    protected abstract void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay);


    @Override
    public void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay,
                                                 MatrixStack matrices, VertexConsumer vertices,
                                                @Nullable Direction withoutFace) {
        for(Direction direction : Direction.values()) {
            if (direction == withoutFace) continue;
            rand.setSeed(42L);
            List<BakedQuad> bakedQuads = model.getQuads(null, direction, rand);
            if (!bakedQuads.isEmpty())
                this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        }
        rand.setSeed(42L);
        List<BakedQuad> bakedQuads = model.getQuads(null, null, rand);
        if (!bakedQuads.isEmpty()) {
            if (withoutFace != null)
                bakedQuads.removeIf(bakedQuad -> bakedQuad.getFace() == withoutFace);
            this.renderBakedItemQuads(matrices, vertices, bakedQuads, stack, light, overlay);
        }
    }

    private boolean canCullTransformation(Transformation transform) {
        if (transform.scale.getX() > 2.0F || transform.scale.getY() > 2.0F || transform.scale.getZ() > 2.0F) {
            return false; //TODO: Maybe Allow Z axis
        }
        if (transform.rotation.getX() % 90 != 0 || transform.rotation.getZ() % 90 != 0 || transform.rotation.getY() % 90 != 0) {
            return false; //TODO: Maybe Allow Y axis, see if the face is correct
        }
        if (transform.translation.getX() != 0 || transform.translation.getY() != 0 || transform.translation.getZ() != 0) {
            return false; //TODO: Maybe allow Z axis, although would require checking scale also
        }
        return true;
    }

    private Direction changeDirectionBasedOnTransformation(Direction dir, Transformation transform) {
        if (transform.rotation.getY() == 0) {
            return dir.getOpposite();
        } else if (transform.rotation.getY() == 90) {
            return dir.rotateYCounterclockwise();
        } else if (transform.rotation.getY() == 270) {
            return dir.rotateYClockwise();
        }
        return dir;
    }


    @Override
    public void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vc,
                                    int light, int seed, boolean shouldCullBack, boolean isInvisible) {
        BakedModel model = this.getModel(stack, null, null, seed);
        matrices.push();
        if (stack.isOf(Items.TRIDENT)) { // Jank mojang checks, yay
            model = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
        } else if (stack.isOf(Items.SPYGLASS)) {
            model = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:spyglass#inventory"));
        }
        Transformation transformation = model.getTransformation().getTransformation(ModelTransformation.Mode.FIXED);
        transformation.apply(false, matrices);
        matrices.translate(-0.5, -0.5, -0.5);
        if (!model.isBuiltin()) {
            boolean isBlockItem = stack.getItem() instanceof BlockItem; //TODO: Do proper checks
            boolean canCull = ((!isBlockItem && !isInvisible) || shouldCullBack) && canCullTransformation(transformation);
            // Use faster cached check for translucency instead of multiple instanceof checks
            boolean bl2 = !isBlockItem || !((BakedOpacity) model).hasTextureTranslucency();
            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
            VertexConsumer vertexConsumer;
            if (stack.isIn(ItemTags.COMPASSES) && stack.hasGlint()) {
                matrices.push();
                MatrixStack.Entry entry = matrices.peek();
                vertexConsumer = bl2 ? getDirectCompassGlintConsumer(vc, renderLayer, entry) :
                        getCompassGlintConsumer(vc, renderLayer, entry);
                matrices.pop();
            } else {
                vertexConsumer = bl2 ? getDirectItemGlintConsumer(vc, renderLayer, true, stack.hasGlint()) :
                        getItemGlintConsumer(vc, renderLayer, true, stack.hasGlint());
            }
            renderBakedItemModelWithoutFace(
                    model,
                    stack,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumer,
                    canCull ? changeDirectionBasedOnTransformation(Direction.NORTH, transformation) : null
            );
        } else {
            this.builtinModelItemRenderer.render(
                    stack,
                    ModelTransformation.Mode.FIXED,
                    matrices,
                    vc,
                    light,
                    OverlayTexture.DEFAULT_UV
            );
        }
        matrices.pop();
    }
}
