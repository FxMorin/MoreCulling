package ca.fxco.moreculling.mixin.items;

import ca.fxco.moreculling.patches.ExtendedItemRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrameEntityRenderer.class)
public class ItemFrameEntityRenderer_cullMixin<T extends ItemFrameEntity> {


    @Redirect(
            method = "render(Lnet/minecraft/entity/decoration/ItemFrameEntity;" +
                    "FFLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(" +
                            "Lnet/minecraft/item/ItemStack;" +
                            "Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;" +
                            "IILnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    public void render(ItemRenderer renderer, ItemStack stack, ModelTransformation.Mode transformationType, int light,
                       int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       int seed, T itemFrameEntity) {
        BlockState blockState = itemFrameEntity.world.getBlockState(itemFrameEntity.getDecorationBlockPos()
                .offset(itemFrameEntity.getHorizontalFacing().getOpposite()));
        ((ExtendedItemRenderer)renderer).renderItemFrameItem(
                stack,
                matrices,
                vertexConsumers,
                light,
                seed,
                blockState.isOpaque(),
                itemFrameEntity.isInvisible()
        );
    }
}
