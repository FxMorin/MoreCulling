package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(targets = "net/minecraft/block/AbstractBlock$AbstractBlockState$ShapeCache")
public class ShapeCache_cullShapeMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOpaque()Z"
            )
    )
    private boolean shouldDoShapeCache(BlockState state) {
        if (blockRenderManager != null && blockRenderManager.getModel(state) != null &&
                ((BakedOpacity) blockRenderManager.getModel(state)).getCullingShape(state) != null) {
            return true;
        }
        return state.isOpaque();
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getCullingShape(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)" +
                            "Lnet/minecraft/util/shape/VoxelShape;"
            )
    )
    private VoxelShape customCullingShape(Block instance, BlockState state, BlockView blockView, BlockPos blockPos) {
        VoxelShape shape = blockRenderManager != null ?
                ((BakedOpacity) blockRenderManager.getModel(state)).getCullingShape(state) : null;
        return shape == null ? instance.getCullingShape(state, blockView, blockPos) : shape;
    }
}
