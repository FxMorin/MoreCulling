package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BrewingStandBlock.class)
public abstract class BrewingStandBlock_cullAgainstMixin implements MoreBlockCulling {
    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return true;
    }
}
