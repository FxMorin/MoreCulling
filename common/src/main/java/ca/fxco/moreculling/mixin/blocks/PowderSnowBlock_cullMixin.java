package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(value = PowderSnowBlock.class, priority = 1200)
public abstract class PowderSnowBlock_cullMixin implements MoreBlockCulling {
    @Override
    public boolean moreculling$usesCustomShouldDrawFace(BlockState state) {
        return true; //Normal powered snow culling will skip this check
    }

    @Override
    public Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState thisState,
                                                              BlockState sideState, BlockPos thisPos,
                                                              BlockPos sidePos, Direction side) {
        return Optional.of(true); //powdersnow will still cull against other blocks using normal shouldDrawFace, so we need to disable it
    }
}
