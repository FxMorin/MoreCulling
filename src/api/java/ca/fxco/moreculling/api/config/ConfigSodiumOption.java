package ca.fxco.moreculling.api.config;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;

/**
 * Extend your ConfigOption with this interface to use a custom sodium Control
 *
 * @since 0.12.0
 */
@Restriction(require = @Condition("sodium"))
public interface ConfigSodiumOption<T> {

    /**
     * The class type that this option should be saved as
     *
     * @since 0.12.0
     */
    Class<T> getTypeClass();

    /**
     * The sodium control that should be used for this option
     *
     * @since 0.12.0
     */
    Control<T> getControl();
}