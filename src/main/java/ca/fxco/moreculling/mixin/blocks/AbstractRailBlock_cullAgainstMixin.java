package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseRailBlock.class)
public class AbstractRailBlock_cullAgainstMixin implements MoreBlockCulling {

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return true;
    }
}
