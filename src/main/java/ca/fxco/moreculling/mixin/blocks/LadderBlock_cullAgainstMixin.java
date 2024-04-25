package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LadderBlock.class)
public class LadderBlock_cullAgainstMixin implements MoreBlockCulling {

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return true;
    }
}
