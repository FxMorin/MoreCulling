package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SignBlock.class)
public class SignBlock_voxelMixin extends Block {
    @Unique
    private static final VoxelShape moreculling$cullingShape = Block.box(7.3F, 0.0F, 7.3F, 8.7F, 16.0, 8.7F);

    public SignBlock_voxelMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return moreculling$cullingShape;
    }
}
