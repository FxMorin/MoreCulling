package ca.fxco.moreculling.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {
  @Accessor("mipmapLevelsImages")
  public NativeImage[] getMipmapLevelsImages();
}
