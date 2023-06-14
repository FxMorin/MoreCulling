package ca.fxco.moreculling.config.option;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public enum LeavesCullingMode implements SelectionListEntry.Translatable {
    DEFAULT("options.gamma.default"),
    FAST("options.clouds.fast"),
    STATE("moreculling.config.options.blockstate"),
    CHECK("moreculling.config.options.check"),
    GAP("moreculling.config.options.gap", true),
    DEPTH("moreculling.config.options.depth", true),
    RANDOM("moreculling.config.options.random", true),
    VERTICAL("moreculling.config.options.vertical");

    private final String translationKey;
    private final boolean hasAmount;

    LeavesCullingMode(String translationKey) {
        this(translationKey, false);
    }

    LeavesCullingMode(String translationKey, boolean hasAmount) {
        this.translationKey = translationKey;
        this.hasAmount = hasAmount;
    }

    public Text getText() {
        return Text.translatable(this.translationKey);
    }

    @Override
    public @NotNull String getKey() {
        return this.translationKey;
    }

    public boolean hasAmount() {
        return this.hasAmount;
    }

    public static Text[] getLocalizedNames() {
        LeavesCullingMode[] values = values();
        Text[] names = new Text[values.length];
        for (int i = 0; i < values.length; i++) names[i] = values[i].getText();
        return names;
    }
}
