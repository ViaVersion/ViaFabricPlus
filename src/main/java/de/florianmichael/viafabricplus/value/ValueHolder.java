package de.florianmichael.viafabricplus.value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.impl.BooleanValue;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ValueHolder {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public final static File CONFIG_FILE = new File(ViaFabricPlus.RUN_DIRECTORY, "settings.json");

    public final static List<AbstractValue<?>> values = new ArrayList<>();

    // General settings
    public static final BooleanValue removeNotAvailableItemsFromCreativeTab = new BooleanValue("Remove not available items from creative tab", true);

    // 1.19 -> 1.18.2
    public static final ProtocolSyncBooleanValue disableSequencing = new ProtocolSyncBooleanValue("Disable sequencing", ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.14 -> 1.13.2
    public static final ProtocolSyncBooleanValue smoothOutMerchantScreens = new ProtocolSyncBooleanValue("Smooth out merchant screens", ProtocolRange.andOlder(ProtocolVersion.v1_13_2));

    // 1.13 -> 1.12.2
    public static final ProtocolSyncBooleanValue executeInputsInSync = new ProtocolSyncBooleanValue("Execute inputs in sync", ProtocolRange.andOlder(ProtocolVersion.v1_12_2));
    public static final ProtocolSyncBooleanValue sneakInstant = new ProtocolSyncBooleanValue("Sneak instant", new ProtocolRange(ProtocolVersion.v1_12_2, ProtocolVersion.v1_8));

    // 1.9 -> 1.8.x
    public static final ProtocolSyncBooleanValue removeCooldowns = new ProtocolSyncBooleanValue("Remove cooldowns", ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public static final ProtocolSyncBooleanValue sendIdlePacket = new ProtocolSyncBooleanValue("Send idle packet", new ProtocolRange(ProtocolVersion.v1_8, LegacyProtocolVersion.r1_3_1tor1_3_2));

    // 1.8.x -> 1.7.6
    public static final ProtocolSyncBooleanValue replaceSneaking = new ProtocolSyncBooleanValue("Replace sneaking", ProtocolRange.andOlder(ProtocolVersion.v1_7_6));
    public static final ProtocolSyncBooleanValue longSneaking = new ProtocolSyncBooleanValue("Long sneaking", ProtocolRange.andOlder(ProtocolVersion.v1_7_6));

    // a1_0_15 -> c0_28toc0_30
    public static final ProtocolSyncBooleanValue useBetaCraftAuthentication = new ProtocolSyncBooleanValue("Use BetaCraft authentication", ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    public static void setup() throws FileNotFoundException {
        if (CONFIG_FILE.exists()) {
            final JsonObject parentNode = GSON.fromJson(new FileReader(CONFIG_FILE), JsonObject.class).getAsJsonObject();
            for (AbstractValue<?> value : values) {
                value.read(parentNode);
            }
        }
    }

    public static void save() throws IOException {
        CONFIG_FILE.delete();
        CONFIG_FILE.createNewFile();

        try (final FileWriter fw = new FileWriter(CONFIG_FILE)) {
            final JsonObject parentNode = new JsonObject();
            for (AbstractValue<?> value : values) {
                value.write(parentNode);
            }
            fw.write(GSON.toJson(parentNode));
            fw.flush();
        }
    }
}
