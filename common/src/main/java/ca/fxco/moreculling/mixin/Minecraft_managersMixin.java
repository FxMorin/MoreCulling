package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.resources.model.ModelManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class Minecraft_managersMixin {

    @Shadow
    @Final
    private ModelManager modelManager;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;modelManager:Lnet/minecraft/client/resources/model/ModelManager;",
                    ordinal = 1,
                    opcode = Opcodes.GETFIELD)
    )
    private void moreculling$onBakedModelManagerInitialized(GameConfig args, CallbackInfo ci) {
        MoreCulling.bakedModelManager = this.modelManager;
    }
}
