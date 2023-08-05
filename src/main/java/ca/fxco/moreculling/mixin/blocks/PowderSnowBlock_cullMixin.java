package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(value = PowderSnowBlock.class, priority = 1200)
public class PowderSnowBlock_cullMixin extends Block implements MoreBlockCulling {

    public PowderSnowBlock_cullMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean usesCustomShouldDrawFace(BlockState state) {
        return MoreCulling.CONFIG.powderSnowCulling; //Normal powered snow culling will skip this check
    }

    @Override
    public Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState,
                                                  BlockPos thisPos, BlockPos sidePos, Direction side) {
        return Optional.of(!(sideState.isOpaque() &&
                sideState.isSideSolidFullSquare(view, sidePos, side.getOpposite())));
    }

    @Override
    public boolean shouldAttemptToCull(BlockState state) {
        return !((BakedOpacity) blockRenderManager.getModel(state)).hasTextureTranslucency(state);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return !MoreCulling.CONFIG.powderSnowCulling ? VoxelShapes.fullCube() : VoxelShapes.empty();
    }
}
