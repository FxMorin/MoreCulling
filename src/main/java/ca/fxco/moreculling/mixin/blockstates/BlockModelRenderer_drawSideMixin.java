package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModelRenderer.class)
public class BlockModelRenderer_drawSideMixin {

    @Unique
    private final ThreadLocal<Boolean> hasTranslucency = new ThreadLocal<>();


    @Inject(
            method = "renderSmooth(Lnet/minecraft/world/BlockRenderView;" +
                    "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;set(" +
                            "Lnet/minecraft/util/math/Vec3i;Lnet/minecraft/util/math/Direction;)" +
                            "Lnet/minecraft/util/math/BlockPos$Mutable;",
                    shift = At.Shift.AFTER
            )
    )
    private void getBakedQuadsSmooth(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos,
                                     MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random,
                                     long seed, int overlay, CallbackInfo ci) {
        this.hasTranslucency.set(((BakedOpacity)model).hasTextureTranslucency());
    }


    @Redirect(
            method = "renderSmooth(Lnet/minecraft/world/BlockRenderView;" +
                    "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"
            )
    )
    private boolean shouldDrawSideSmooth(BlockState state, BlockView world,
                                         BlockPos pos, Direction side, BlockPos otherPos) {
        return CullingUtils.shouldDrawSideCulling(state, world, pos, side, otherPos, hasTranslucency.get());
    }


    @Inject(
            method = "renderFlat(Lnet/minecraft/world/BlockRenderView;" +
                    "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;set(" +
                            "Lnet/minecraft/util/math/Vec3i;Lnet/minecraft/util/math/Direction;)" +
                            "Lnet/minecraft/util/math/BlockPos$Mutable;",
                    shift = At.Shift.AFTER
            )
    )
    private void getBakedQuadsFlat(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos,
                                   MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random,
                                   long seed, int overlay, CallbackInfo ci) {
        this.hasTranslucency.set(((BakedOpacity)model).hasTextureTranslucency());
    }


    @Redirect(
            method = "renderFlat(Lnet/minecraft/world/BlockRenderView;" +
                    "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"
            )
    )
    private boolean shouldDrawSideFlat(BlockState state, BlockView world,
                                       BlockPos pos, Direction side, BlockPos otherPos) {
        return CullingUtils.shouldDrawSideCulling(state, world, pos, side, otherPos, hasTranslucency.get());
    }
}
