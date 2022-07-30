package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MoreCullingConfigCategory implements ConfigCategory {
    private final ConfigBuilder builder;
    private final List<Object> data;
    @Nullable
    private Identifier background;
    private final Text categoryKey;
    @Nullable
    private Supplier<Optional<StringVisitable[]>> description = Optional::empty;

    public MoreCullingConfigCategory(ConfigBuilder builder, Text categoryKey) {
        this.builder = builder;
        this.data = Lists.newArrayList();
        this.categoryKey = categoryKey;
    }

    public Text getCategoryKey() {
        return this.categoryKey;
    }

    public List<Object> getEntries() {
        return this.data;
    }

    public ConfigCategory addEntry(AbstractConfigListEntry entry) {
        this.data.add(entry);
        return this;
    }

    public ConfigCategory setCategoryBackground(Identifier identifier) {
        if (this.builder.hasTransparentBackground()) {
            throw new IllegalStateException("Cannot set category background if screen is using transparent background.");
        } else {
            this.background = identifier;
            return this;
        }
    }

    public void removeCategory() {
        this.builder.removeCategory(this.categoryKey);
    }

    public void setBackground(@Nullable Identifier background) {
        this.background = background;
    }

    @Nullable
    public Identifier getBackground() {
        return this.background;
    }

    @Nullable
    public Supplier<Optional<StringVisitable[]>> getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable Supplier<Optional<StringVisitable[]>> description) {
        this.description = description;
    }
}
