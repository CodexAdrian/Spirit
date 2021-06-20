package me.codexadrian.spirit.compat;

import me.shedaniel.rei.api.common.plugins.REIPlugin;
import org.jetbrains.annotations.NotNull;

public class REISpiritPlugin implements REIPlugin {

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    @Override
    public Class getPluginProviderClass() {
        return null;
    }
}
