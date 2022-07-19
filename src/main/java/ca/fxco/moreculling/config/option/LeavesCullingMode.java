package ca.fxco.moreculling.config.option;

import me.jellysquid.mods.sodium.client.gui.options.TextProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum LeavesCullingMode implements TextProvider {
    DEFAULT("options.gamma.default"),
    FAST("options.clouds.fast"),
    STATE("moreculling.config.options.blockstate"),
    CHECK("moreculling.config.options.check"),
    DEPTH("moreculling.config.options.depth");

    private final Text name;

    LeavesCullingMode(String name) {
        this.name = Text.translatable(name);
    }

    @Override
    public Text getLocalizedName() {
        return this.name;
    }
}
