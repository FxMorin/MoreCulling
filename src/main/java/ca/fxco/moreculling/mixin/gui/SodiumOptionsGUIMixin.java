package ca.fxco.moreculling.mixin.gui;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.SodiumOptionPage;
import ca.fxco.moreculling.utils.CacheUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static ca.fxco.moreculling.utils.CompatUtils.IS_MODERNFIX_LOADED;

@Restriction(require = @Condition("sodium"))
@Mixin(SodiumOptionsGUI.class)
public class SodiumOptionsGUIMixin extends Screen {

    @Shadow(remap = false)
    @Final
    private List<OptionPage> pages;

    @Shadow(remap = false)
    private OptionPage currentPage;

    private OptionPage moreCullingPage;
    private FlatButtonWidget resetCacheButton;

    protected SodiumOptionsGUIMixin(Text title) {
        super(title);
    }


    @Inject(
            method = "<init>(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At("RETURN")
    )
    private void addGuiAtInit(Screen prevScreen, CallbackInfo ci) {
        if (MoreCulling.CONFIG.enableSodiumMenu) {
            this.moreCullingPage = SodiumOptionPage.moreCullingPage();
            this.pages.add(this.moreCullingPage); // Inject sodium page for moreCulling
        }
    }


    @Inject(
            method = "rebuildGUI()V",
            at = @At("RETURN"),
            remap = false,
            require = 0
    )
    private void addCacheRefreshButton(CallbackInfo ci) {
        if (IS_MODERNFIX_LOADED) {
            return;
        }
        if (MoreCulling.CONFIG.enableSodiumMenu && this.currentPage == this.moreCullingPage) {
            // 325 is the last button (211) + width (100) plus padding (20 + 4)
            this.addDrawableChild(this.resetCacheButton = new FlatButtonWidget(new Dim2i(this.width - 325, this.height - 30, 100, 20), Text.translatable("moreculling.config.resetCache"), () -> {
                CacheUtils.resetAllCache();
                this.resetCacheButton.setEnabled(false);
            }));
        }
    }
}
