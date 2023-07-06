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
package de.florianmichael.viafabricplus.base.settings.groups;

import de.florianmichael.viafabricplus.base.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.base.settings.type_impl.BooleanSetting;
import de.florianmichael.viafabricplus.base.settings.type_impl.ButtonSetting;
import de.florianmichael.viafabricplus.definition.bedrock.BedrockAccountHandler;
import de.florianmichael.viafabricplus.screen.impl.base.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.screen.impl.settings.SettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.step.msa.StepMsaDeviceCode;
import net.raphimc.mcauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class BedrockSettings extends SettingGroup {
    public final static BedrockSettings INSTANCE = new BedrockSettings();

    public final ButtonSetting BEDROCK_ACCOUNT = new ButtonSetting(this, Text.translatable("bedrock.viafabricplus.authentication"), () -> CompletableFuture.runAsync(() -> {
        try {
            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                final var mcChain = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                    MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ConfirmScreen(consumer -> {
                        if (consumer) {
                            MinecraftClient.getInstance().keyboard.setClipboard(msaDeviceCode.userCode());
                        } else {
                            SettingsScreen.INSTANCE.open(new MultiplayerScreen(new TitleScreen()));
                            Thread.currentThread().interrupt();
                        }
                    }, Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.text", msaDeviceCode.userCode()), Text.translatable("words.viafabricplus.copy"), Text.translatable("words.viafabricplus.cancel"))));
                    try {
                        Util.getOperatingSystem().open(new URI(msaDeviceCode.verificationUri()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> Thread.currentThread().interrupt(), Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.error"), Text.translatable("words.viafabricplus.cancel"), false)));
                    }
                }));
                BedrockAccountHandler.INSTANCE.setAccount(mcChain, MinecraftAuth.BEDROCK_PLAY_FAB_TOKEN.getFromInput(httpClient, mcChain.prevResult().fullXblSession()));
            }
            ProtocolSelectionScreen.INSTANCE.open(new MultiplayerScreen(new TitleScreen()));
        } catch (Throwable e) {
            e.printStackTrace();
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> Thread.currentThread().interrupt(), Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.error"), Text.translatable("words.viafabricplus.cancel"), false)));
        }
    })) {
        @Override
        public MutableText displayValue() {
            if (BedrockAccountHandler.INSTANCE.getMcChain() != null) {
                return Text.literal("Bedrock account: " + BedrockAccountHandler.INSTANCE.getMcChain().displayName());
            }
            return super.displayValue();
        }
    };
    public final BooleanSetting confirmServerTransferInBedrockEdition = new BooleanSetting(this, Text.translatable("bedrock.viafabricplus.confirmtransfer"), true);

    public BedrockSettings() {
        super(Text.translatable("settings.viafabricplus.bedrock"));
    }
}
