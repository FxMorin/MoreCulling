package ca.fxco.moreculling.api.config;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Add your config options to this class to add them to the MoreCulling config
 *
 * @since 0.12.0
 */

public class ConfigAdditions {

    private static final Map<String, List<ConfigOption<?>>> additionOptions = new LinkedHashMap<>();
    private static final Map<String, Pair<String, Function<?, ?>>> disabledOptions = new HashMap<>();
    private static final HashSet<String> separateGroupTabs = new HashSet<>();

    /**
     * Use this method to add option to the MoreCulling config
     *
     * @since 0.12.0
     */
    public static void addOption(String group, ConfigOption<?> option) {
        ConfigAdditions.additionOptions.computeIfAbsent(group, g -> new LinkedList<>()).add(option);
    }

    /**
     * Use this method to disable an option in the MoreCulling config.
     * @param id        The option to be disabled. This will attempt to match against the option name, if the option
     *                  uses a translation key, it will attempt to match the translation key.
     * @param reason    The reason why this option was disabled.
     * @param setOption A function that can be used to set the options value.
     *
     * @since 0.23.0
     */
    public static <T> void disableOption(String id, String reason, @Nullable Function<T, T> setOption) {
        ConfigAdditions.disabledOptions.put(id, Pair.of(reason, setOption));
    }

    /**
     * Allows you to set if a group should be separated into its own tab.
     * This only works in mod menu
     *
     * @since 0.12.0
     */
    public static void useSeparateTab(String group) {
        ConfigAdditions.separateGroupTabs.add(group);
    }

    /**
     * Makes a group not use a separate tab
     *
     * @since 0.12.0
     */
    public static void disableSeparateTab(String group) {
        ConfigAdditions.separateGroupTabs.remove(group);
    }

    /**
     * Returns if this group will be separated into its own tab
     *
     * @since 0.12.0
     */
    public static boolean isGroupSeparate(String group) {
        return ConfigAdditions.separateGroupTabs.contains(group);
    }

    /**
     * This is for internal use only
     *
     * @since 0.12.0
     */
    @ApiStatus.Internal
    public static Map<String, List<ConfigOption<?>>> getOptions() {
        return ConfigAdditions.additionOptions;
    }

    /**
     * This is for internal use only
     *
     * @since 0.23.0
     */
    @ApiStatus.Internal
    public static Map<String, Pair<String, Function<?, ?>>> getDisabledOptions() {
        return ConfigAdditions.disabledOptions;
    }
}
