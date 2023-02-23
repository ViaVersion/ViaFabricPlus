package de.florianmichael.viafabricplus.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;

public class ViaFabricPlusEncryptionProvider extends EncryptionProvider {

    @Override
    public void enableDecryption(UserConnection user) {
        final ClientConnection clientConnection = user.getChannel().attr(ViaFabricPlus.LOCAL_MINECRAFT_CONNECTION).get();
        if (clientConnection != null) {
            ((IClientConnection) clientConnection).viafabricplus_setupPreNettyEncryption();
        }
    }
}
