package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public class DoorBlock_cullAgainstMixin implements MoreBlockCulling {

    @Override
    public boolean cantCullAgainst(BlockState state, Direction side) {
        return true;
    }
}
