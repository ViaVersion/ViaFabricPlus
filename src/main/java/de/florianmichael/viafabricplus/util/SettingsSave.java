package de.florianmichael.viafabricplus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.AbstractSetting;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;

import java.io.*;

public class SettingsSave {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public final static File CONFIG_FILE = new File(ViaFabricPlus.RUN_DIRECTORY, "settings.json");

    public static void load(final ViaFabricPlus viaFabricPlus) throws Exception {
        if (CONFIG_FILE.exists()) {
            final JsonObject parentNode = GSON.fromJson(new FileReader(CONFIG_FILE), JsonObject.class).getAsJsonObject();
            if (parentNode.has("protocol")) {
                ViaLoadingBase.getClassWrapper().reload(InternalProtocolList.fromProtocolId(parentNode.get("protocol").getAsInt()));
            }
            for (SettingGroup group : viaFabricPlus.getSettingGroups()) {
                for (AbstractSetting<?> setting : group.getSettings()) {
                    setting.read(parentNode);
                }
            }
        }
    }

    public static void save(final ViaFabricPlus viaFabricPlus) throws IOException {
        CONFIG_FILE.delete();
        CONFIG_FILE.createNewFile();

        try (final FileWriter fw = new FileWriter(CONFIG_FILE)) {
            final JsonObject parentNode = new JsonObject();
            parentNode.addProperty("protocol", ViaLoadingBase.getClassWrapper().getTargetVersion().getVersion());
            for (SettingGroup group : viaFabricPlus.getSettingGroups()) {
                for (AbstractSetting<?> setting : group.getSettings()) {
                    setting.write(parentNode);
                }
            }
            fw.write(GSON.toJson(parentNode));
            fw.flush();
        }
    }
}
