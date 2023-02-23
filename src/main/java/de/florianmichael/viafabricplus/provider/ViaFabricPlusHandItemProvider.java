package de.florianmichael.viafabricplus.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import de.florianmichael.viafabricplus.translator.ItemTranslator;
import net.minecraft.item.ItemStack;

public class ViaFabricPlusHandItemProvider extends HandItemProvider {
    public static ItemStack lastUsedItem = null;

    @Override
    public Item getHandItem(UserConnection info) {
        if (lastUsedItem == null) {
            return null;
        }
        return ItemTranslator.minecraftToViaVersion(info, lastUsedItem, ProtocolVersion.v1_8.getVersion());
    }
}
