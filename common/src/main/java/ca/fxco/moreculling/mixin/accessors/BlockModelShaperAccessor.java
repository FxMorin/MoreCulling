package ca.fxco.moreculling.mixin.accessors;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public interface BlockModelShaperAccessor {

    @Accessor("modelByStateCache")
    Map<BlockState, BlockStateModel> getModels();
}
