package de.florianmichael.viafabricplus.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

public class ScreenUtil {

    public static void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public static String prefixedMessage(final String message) {
        return Formatting.GOLD + "[ViaFabricPlus] " + Formatting.WHITE + message;
    }
}
