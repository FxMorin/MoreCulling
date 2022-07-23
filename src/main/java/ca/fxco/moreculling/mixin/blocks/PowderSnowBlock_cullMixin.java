package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

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
        return Optional.of(!(sideState.isOpaque() && sideState.isSideSolidFullSquare(view, sidePos, side)));
    }
}
