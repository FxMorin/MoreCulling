package ca.fxco.moreculling.mixin.models.cullshape;

import ca.fxco.moreculling.api.model.BakedOpacity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBase_cullShapeMixin {

    @Shadow public abstract Block getBlock();

    @Shadow protected abstract BlockState asState();

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

    @WrapOperation(
            method = "initCache",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/shapes/Shapes;empty()Lnet/minecraft/world/phys/shapes/VoxelShape;"
            )
    )
    private VoxelShape moreculling$cacheOcclusionShape(Operation<VoxelShape> original) {
        return this.getBlock().getOcclusionShape(this.asState());
    }
}
