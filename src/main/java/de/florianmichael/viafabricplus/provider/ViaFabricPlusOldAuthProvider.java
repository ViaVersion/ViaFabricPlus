package de.florianmichael.viafabricplus.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.groups.MPPassSettings;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;

public class ViaFabricPlusOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        if (!MPPassSettings.getClassWrapper().allowViaLegacyToCallJoinServerToVerifySession.getValue()) return;

        final MinecraftClient mc = MinecraftClient.getInstance();

        try {
            mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getAccessToken(), serverId);
        } catch (Exception e) {
            if (MPPassSettings.getClassWrapper().disconnectIfJoinServerCallFails.getValue()) {
                user.getChannel().attr(ViaFabricPlus.LOCAL_MINECRAFT_CONNECTION).get().disconnect(Text.literal(ScreenUtil.prefixedMessage("ViaLegacy fails to verify your session! Please log in into an Account or disable the BetaCraft authentication in the ViaFabricPlus Settings")));
            } else {
                e.printStackTrace();
            }
        }
    }
}
