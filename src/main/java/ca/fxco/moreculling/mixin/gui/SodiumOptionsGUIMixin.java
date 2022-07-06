package ca.fxco.moreculling.mixin.gui;

import ca.fxco.moreculling.config.sodium.SodiumOptionPage;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(require = @Condition("sodium"))
@Mixin(SodiumOptionsGUI.class)
public class SodiumOptionsGUIMixin {

    @Shadow
    @Final
    private List<OptionPage> pages;


    @Inject(
            method = "<init>(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At("RETURN")
    )
    private void addGuiAtInit(Screen prevScreen, CallbackInfo ci) {
        this.pages.add(SodiumOptionPage.moreCullingPage()); // Inject sodium page for moreCulling
    }
}
