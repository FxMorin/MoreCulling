package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBase_cullShapeMixin {

    @Redirect(
            method = "initCache",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;canOcclude:Z"
            )
    )
    private boolean moreculling$shouldDoShapeCache(BlockBehaviour.BlockStateBase instance) {
        BlockState state = (BlockState) instance;
        if (blockRenderManager != null) {
            BakedModel model = blockRenderManager.getBlockModel(state);
            if (model != null && ((BakedOpacity) model).moreculling$getCullingShape(state) != null) {
                return true;
            }
        }
        return state.canOcclude();
    }

    @Redirect(
            method = "initCache",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getOcclusionShape(" +
                            "Lnet/minecraft/world/level/block/state/BlockState;)" +
                            "Lnet/minecraft/world/phys/shapes/VoxelShape;"
            )
    )
    private VoxelShape moreculling$customCullingShape(Block instance, BlockState state) {
        if (blockRenderManager != null) {
            BakedModel model = blockRenderManager.getBlockModel(state);
            if (model != null) {
                VoxelShape voxelShape = ((BakedOpacity) model).moreculling$getCullingShape(state);
                if (voxelShape != null) {
                    return voxelShape;
                }
            }
        }
        return instance.getOcclusionShape(state);
    }
}
