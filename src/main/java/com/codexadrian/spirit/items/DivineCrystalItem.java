package com.codexadrian.spirit.items;

import com.codexadrian.spirit.Spirit;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DivineCrystalItem extends Item {

    public DivineCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        if (itemStack.hasTag()) {
            final CompoundTag storedEntity = itemStack.getTag().getCompound("StoredEntity");
                if (storedEntity.contains("Type")) {
                    MutableComponent tooltip = new TranslatableComponent(Util.makeDescriptionId("entity", new ResourceLocation(storedEntity.getString("Type"))));
                    tooltip.append(new TextComponent(" " + (Spirit.getTier(itemStack)) + " - "));
                    if(!Screen.hasShiftDown()) {
                        tooltip.append(new TextComponent("(" + getPercentage(itemStack) + "%) "));
                    } else {
                        tooltip.append(new TextComponent("(" + storedEntity.getInt("Souls") + "/" + Math.min(Spirit.getSpiritConfig().getRequiredSouls()*(Spirit.getTier(itemStack) + 1), Spirit.getSpiritConfig().getMaxSouls()) + ") "));
                    }
                    list.add(tooltip.withStyle(ChatFormatting.GRAY));
                }
        } else {
            MutableComponent unboundTooltip = new TextComponent("Unbound");
            list.add(unboundTooltip.withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
        }
    }

    public static double getPercentage(ItemStack itemStack) {
        int storedSouls = itemStack.getTag().getCompound("StoredEntity").getInt("Souls");
        int tier = Spirit.getTier(itemStack);
        double percentage = ((double)storedSouls/((tier +1)* Spirit.getSpiritConfig().getRequiredSouls())) * 100;
        double p = percentage*10;
        int p2 = (int)p;
        return tier == 4 ? 100 : (double)p2/10;
    }
}
