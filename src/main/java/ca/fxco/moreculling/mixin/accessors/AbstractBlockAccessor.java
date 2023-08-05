package ca.fxco.moreculling.mixin.accessors;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface AbstractBlockAccessor {

    @Accessor("collidable")
    boolean getCollidable();
}