package ca.fxco.moreculling.patches;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

//TODO: Major Cleanup & put into the public API

public interface ExtendedItemRenderer {
    void renderItemFrameItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vc, int light, int seed, ItemFrameEntity frame, Vec3d cameraPos);
    void renderBakedItemModelWithoutFace(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices, @Nullable Direction withoutFace);
    void renderBakedItemModelForFace(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices, Direction face);
}