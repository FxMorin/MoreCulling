package ca.fxco.moreculling.mixin.accessors;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {

    @Invoker("getOcclusionShape")
    VoxelShape moreculling$getOcclusionShape(BlockState state);
}
