package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(value = PowderSnowBlock.class, priority = 1200)
public abstract class PowderSnowBlock_cullMixin extends Block implements MoreBlockCulling {

    public PowderSnowBlock_cullMixin(Properties settings) {
        super(settings);
    }

    @Override
    public boolean moreculling$usesCustomShouldDrawFace(BlockState state) {
        return true; //Normal powered snow culling will skip this check
    }

    @Override
    public Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState thisState,
                                                              BlockState sideState, BlockPos thisPos,
                                                              BlockPos sidePos, Direction side) {
        return Optional.of(!sideState.is(this));
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return MoreCulling.CONFIG.powderSnowCulling ? Shapes.block() : Shapes.empty();
    }
}
