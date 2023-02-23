package de.florianmichael.viafabricplus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.value.AbstractValue;
import de.florianmichael.viafabricplus.value.ValueHolder;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList;

import java.io.*;

public class SavingSystem {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public final static File CONFIG_FILE = new File(ViaFabricPlus.RUN_DIRECTORY, "settings.json");

    public static void setup() throws FileNotFoundException {
        if (CONFIG_FILE.exists()) {
            final JsonObject parentNode = GSON.fromJson(new FileReader(CONFIG_FILE), JsonObject.class).getAsJsonObject();
            if (parentNode.has("protocol")) {
                ViaLoadingBase.getClassWrapper().reload(InternalProtocolList.fromProtocolId(parentNode.get("protocol").getAsInt()));
            }
            for (AbstractValue<?> value : ValueHolder.values) {
                value.read(parentNode);
            }
        }
    }

    public static void save() throws IOException {
        CONFIG_FILE.delete();
        CONFIG_FILE.createNewFile();

        try (final FileWriter fw = new FileWriter(CONFIG_FILE)) {
            final JsonObject parentNode = new JsonObject();
            parentNode.addProperty("protocol", ViaLoadingBase.getTargetVersion().getVersion());
            for (AbstractValue<?> value : ValueHolder.values) {
                value.write(parentNode);
            }
            fw.write(GSON.toJson(parentNode));
            fw.flush();
        }
    }
}
