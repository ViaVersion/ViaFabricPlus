package de.florianmichael.viafabricplus.definition.c0_30;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.storage.ClassicProgressStorage;

public class ClassicProgressRenderer extends DrawableHelper {

    public static void renderProgress(final MatrixStack matrices) {
        if (MinecraftClient.getInstance().getNetworkHandler() == null) return;
        final UserConnection connection = MinecraftClient.getInstance().getNetworkHandler().getConnection().channel.attr(ViaFabricPlus.LOCAL_VIA_CONNECTION).get();
        if (connection == null) return;
        final ClassicProgressStorage classicProgressStorage = connection.get(ClassicProgressStorage.class);
        if (classicProgressStorage == null) return;

        final Window window = MinecraftClient.getInstance().getWindow();
        drawCenteredText(
                matrices,
                MinecraftClient.getInstance().textRenderer,
                "[ViaFabricPlus] " + classicProgressStorage.status,
                window.getScaledWidth() / 2,
                window.getScaledHeight() / 2 - 30,
                -1
                );
    }
}
