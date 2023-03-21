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

import de.florianmichael.viafabricplus.definition.bedrock.BedrockAccountHandler;
import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.type_impl.ButtonSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.raphimc.mcauth.MinecraftAuth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class BedrockSettings extends SettingGroup {
    public final static BedrockSettings INSTANCE = new BedrockSettings();

    public final ButtonSetting BEDROCK_ACCOUNT = new ButtonSetting(this, Text.translatable("bedrock.viafabricplus.set"), () -> CompletableFuture.runAsync(() -> {
        try {
            BedrockAccountHandler.INSTANCE.setAccount(MinecraftAuth.requestBedrockLogin(msaDeviceCode -> {
                MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> {
                    MinecraftClient.getInstance().setScreen(SettingsScreen.get(new MultiplayerScreen(new TitleScreen())));
                    Thread.currentThread().interrupt();
                }, Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.text", msaDeviceCode.userCode()), Text.translatable("words.viafabricplus.cancel"), false)));
                try {
                    Util.getOperatingSystem().open(new URI(msaDeviceCode.verificationUri()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> {
                        Thread.currentThread().interrupt();
                    }, Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.error"), Text.translatable("words.viafabricplus.cancel"), false)));
                }
            }));
            ProtocolSelectionScreen.open(new MultiplayerScreen(new TitleScreen()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    })) {
        @Override
        public MutableText displayValue() {
            if (BedrockAccountHandler.INSTANCE.getAccount() != null) {
                return Text.literal("Bedrock account: " + BedrockAccountHandler.INSTANCE.getAccount().displayName());
            }
            return super.displayValue();
        }
    };

    public BedrockSettings() {
        super("Bedrock");
    }
}
