package ca.fxco.moreculling.mixin.compat;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Restriction(require = @Condition("enhancedblockentities"))
@Mixin(SignBlock.class)
public class SignBlock_ebeMixin extends Block {
    @Unique
    private static final VoxelShape moreculling$cullingShape = Block.box(7.3F, 0.0F, 7.3F, 8.7F, 16.0, 8.7F);

    public SignBlock_ebeMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return moreculling$cullingShape;
    }
}
