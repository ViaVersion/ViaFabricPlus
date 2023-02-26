package de.florianmichael.viafabricplus.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.florianmichael.viafabricplus.screen.settings.ValuesScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) parent -> {
            ValuesScreen.INSTANCE.prevScreen = parent;
            return ValuesScreen.INSTANCE;
        };
    }
}
