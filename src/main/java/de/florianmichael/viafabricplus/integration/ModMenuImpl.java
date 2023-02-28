package de.florianmichael.viafabricplus.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) parent -> {
            SettingsScreen.INSTANCE.prevScreen = parent;
            return SettingsScreen.INSTANCE;
        };
    }
}
