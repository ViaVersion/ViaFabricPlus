package de.florianmichael.viafabricplus.definition;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class ChatLengthDefinition {
    private static int maxLength = 256;

    public static void reload(final ComparableProtocolVersion protocolVersion) {
        maxLength = 256;
        if (protocolVersion.isOlderThanOrEqualTo(ProtocolVersion.v1_10)) {
            maxLength = 100;

            if (protocolVersion.isOlderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                maxLength = 64 - MinecraftClient.getInstance().getSession().getUsername().length() - 2;
            }
        }
    }

    public static void expand() {
        maxLength = Short.MAX_VALUE * 2;
    }

    public static int getMaxLength() {
        return maxLength;
    }
}
