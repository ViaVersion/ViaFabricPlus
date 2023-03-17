/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.settings.groups;

import de.florianmichael.viafabricplus.definition.bedrock.BedrockAccountManager;
import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.type_impl.ButtonSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.raphimc.mcauth.MinecraftAuth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class BedrockSettings extends SettingGroup {
    public final static BedrockSettings INSTANCE = new BedrockSettings();

    public final ButtonSetting BEDROCK_ACCOUNT = new ButtonSetting(this, "Click to set account for Bedrock edition", () -> CompletableFuture.runAsync(() -> {
        try {
            BedrockAccountManager.INSTANCE.setAccount(MinecraftAuth.requestBedrockLogin(msaDeviceCode -> {
                MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> {
                    MinecraftClient.getInstance().setScreen(SettingsScreen.get(new MultiplayerScreen(new TitleScreen())));
                    Thread.currentThread().interrupt();
                }, Text.literal("Microsoft Bedrock login"), Text.literal("Your browser should have opened.\nPlease enter the following Code: " + Formatting.BOLD + msaDeviceCode.userCode() + Formatting.RESET + "\nClosing this screen will cancel the process!"), Text.literal("Cancel"), false)));
                try {
                    Util.getOperatingSystem().open(new URI(msaDeviceCode.verificationUri()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }));
            ProtocolSelectionScreen.open(new MultiplayerScreen(new TitleScreen()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    })) {
        @Override
        public String displayValue() {
            if (BedrockAccountManager.INSTANCE.getAccount() != null) {
                return super.displayValue() + ", current: " + BedrockAccountManager.INSTANCE.getAccount().displayName();
            }
            return super.displayValue();
        }
    };

    public BedrockSettings() {
        super("Bedrock");
    }
}
