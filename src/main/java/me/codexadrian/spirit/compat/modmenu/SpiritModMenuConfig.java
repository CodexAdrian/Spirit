package me.codexadrian.spirit.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class SpiritModMenuConfig implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new CottonClientScreen(new SpiritConfigScreen(parent)) {
            @Override
            public void onClose() {
                assert this.minecraft != null;
                this.minecraft.setScreen(parent);
            }
        };
    }
}
