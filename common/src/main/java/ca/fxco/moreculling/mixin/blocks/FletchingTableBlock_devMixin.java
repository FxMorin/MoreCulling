package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlock_devMixin implements MoreBlockCulling {

    //Place fletching tables against blocks to see if other blocks cull correctly against full blocks

    @Override
    public boolean moreculling$usesCustomShouldDrawFace(BlockState state) {
        return Services.PLATFORM.isDevelopmentEnvironment(); // Dev enviroment only
    }

    @Override
    public Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState thisState,
                                                              BlockState sideState, BlockPos thisPos,
                                                              BlockPos sidePos, Direction side) {
        return Optional.of(false);
    }
}
