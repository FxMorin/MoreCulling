package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlock_devMixin implements MoreBlockCulling {

    //Place fletching tables against blocks to see if other blocks cull correctly against full blocks

    @Override
    public boolean usesCustomShouldDrawFace(BlockState state) {
        return FabricLoader.getInstance().isDevelopmentEnvironment(); // Dev enviroment only
    }

    @Override
    public Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState,
                                                  BlockPos thisPos, BlockPos sidePos, Direction side) {
        return Optional.of(false);
    }

}
