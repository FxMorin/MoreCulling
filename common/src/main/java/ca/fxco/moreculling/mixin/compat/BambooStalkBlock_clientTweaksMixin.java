package ca.fxco.moreculling.mixin.compat;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(require = @Condition("clienttweaks"))
@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlock_clientTweaksMixin extends Block {

    public BambooStalkBlock_clientTweaksMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return state.getOcclusionShape();
    }
}
