package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(targets = "net/minecraft/block/AbstractBlock$AbstractBlockState$ShapeCache")
public class ShapeCache_cullShapeMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOpaque()Z"
            )
    )
    private boolean shouldDoShapeCache(BlockState state, Operation<Boolean> original) {
        if (blockRenderManager != null) {
            BakedModel model = blockRenderManager.getModel(state);
            if (model != null && ((BakedOpacity) model).getCullingShape(state) != null) {
                return true;
            }
        }
        return original.call(state);
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getCullingShape(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)" +
                            "Lnet/minecraft/util/shape/VoxelShape;"
            )
    )
    private VoxelShape customCullingShape(Block instance, BlockState state, BlockView blockView, BlockPos blockPos, Operation<VoxelShape> original) {
        if (blockRenderManager != null) {
            BakedModel model = blockRenderManager.getModel(state);
            if (model != null) {
                VoxelShape voxelShape = ((BakedOpacity) model).getCullingShape(state);
                if (voxelShape != null) {
                    return voxelShape;
                }
            }
        }
        return original.call(instance, state, blockView, blockPos);
    }
}
