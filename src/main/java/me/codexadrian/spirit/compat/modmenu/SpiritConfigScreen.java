package me.codexadrian.spirit.compat.modmenu;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import me.codexadrian.spirit.Spirit;
import me.codexadrian.spirit.SpiritClient;
import me.codexadrian.spirit.SpiritClientConfig;
import me.codexadrian.spirit.SpiritConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import static me.codexadrian.spirit.Spirit.MODID;

public class SpiritConfigScreen extends LightweightGuiDescription {
    public SpiritConfigScreen(Screen previous) {
        WPlainPanel root = new WPlainPanel();
        root.setInsets(Insets.ROOT_PANEL);
        setRootPanel(root);

        WToggleButton shaderButton = new WToggleButton() {
            @Override
            protected void onToggle(boolean on) {
                SpiritClient.getClientConfig().setShaderStatus(on);
                SpiritClient.saveConfig(SpiritClient.getClientConfig());
            }
            @Environment(EnvType.CLIENT)
            @Override
            public void addTooltip(TooltipBuilder tooltip) {
                tooltip.add(new TranslatableComponent("config.spirit.toggletooltip"));
            }
        };
        shaderButton.setToggle(SpiritClient.getClientConfig().getShaderStatus());
        root.add(shaderButton, 27, 4 * 18, 18, 18);

        WSprite icon = new WSprite(new ResourceLocation(MODID, "icon.png"));
        root.add(icon, 0, 0, 4 * 18, 4 * 18);

        WText text = new WText(new TextComponent("Spirit"));
        text.setColor(0x00b8c2, 0x00b8c2);
        root.add(text, 24, 5 * 18, 3 * 18, 18);

        this.addPainters();
        root.setSize(5*18, 7*18);
        root.validate(this);
    }

    @Override
    public void addPainters() {
        getRootPanel().setBackgroundPainter(null);
    }
}
