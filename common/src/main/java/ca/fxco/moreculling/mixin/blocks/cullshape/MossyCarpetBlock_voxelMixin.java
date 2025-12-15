package ca.fxco.moreculling.mixin.blocks.cullshape;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MossyCarpetBlock.class)
public class MossyCarpetBlock_voxelMixin extends Block {

    public MossyCarpetBlock_voxelMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state) {
        return super.getOcclusionShape(defaultBlockState());
    }
}
