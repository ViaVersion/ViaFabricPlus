/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.storage.ClassicProgressStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(targets = { "net.minecraft.client.gui.screen.DownloadingTerrainScreen", "net.minecraft.client.gui.screen.ConnectScreen" })
public class MixinDownloadingTerrainScreenAndConnectScreen extends Screen {

    public MixinDownloadingTerrainScreenAndConnectScreen(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void renderClassicProgress(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (GeneralSettings.INSTANCE.showClassicLoadingProgressInConnectScreen.getValue()) {
            if (client.getNetworkHandler() == null) return;

            final var connection = ProtocolHack.getMainUserConnection();
            if (connection == null) return;

            final var classicProgressStorage = connection.get(ClassicProgressStorage.class);
            if (classicProgressStorage == null) return;

            context.drawCenteredTextWithShadow(client.textRenderer, ChatUtil.prefixText(classicProgressStorage.status), width / 2, height / 2 - 30, -1);
        }
    }
}
