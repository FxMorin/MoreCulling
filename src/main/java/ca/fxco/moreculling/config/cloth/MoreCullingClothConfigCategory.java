package ca.fxco.moreculling.config.cloth;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MoreCullingClothConfigCategory implements ConfigCategory {
    private final ConfigBuilder builder;
    private final List<Object> data;
    @Nullable
    private ResourceLocation background;
    private final Component categoryKey;
    @Nullable
    private Supplier<Optional<FormattedText[]>> description = Optional::empty;

    public MoreCullingClothConfigCategory(ConfigBuilder builder, Component categoryKey) {
        this.builder = builder;
        this.data = Lists.newArrayList();
        this.categoryKey = categoryKey;
    }

    public Component getCategoryKey() {
        return this.categoryKey;
    }

    public List<Object> getEntries() {
        return this.data;
    }

    public ConfigCategory addEntry(AbstractConfigListEntry entry) {
        this.data.add(entry);
        return this;
    }

    public ConfigCategory setCategoryBackground(ResourceLocation identifier) {
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

    public void setBackground(@Nullable ResourceLocation background) {
        this.background = background;
    }

    @Nullable
    public ResourceLocation getBackground() {
        return this.background;
    }

    @Nullable
    public Supplier<Optional<FormattedText[]>> getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable Supplier<Optional<FormattedText[]>> description) {
        this.description = description;
    }
}
