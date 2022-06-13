package ca.fxco.moreculling.patches;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface ExtendedItemRenderer {
    void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vc, int light, int seed, boolean shouldCullBack, boolean isInvisible);
    void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices, @Nullable Direction withoutFace);
}