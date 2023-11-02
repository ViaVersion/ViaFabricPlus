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
package de.florianmichael.viafabricplus.settings.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.type.BooleanSetting;
import de.florianmichael.viafabricplus.settings.type.ButtonSetting;
import de.florianmichael.viafabricplus.definition.account.BedrockAccountHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
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
        final var prevScreen = MinecraftClient.getInstance().currentScreen;
        try {
            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                final var mcChain = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                    MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ConfirmScreen(consumer -> {
                        if (consumer) {
                            MinecraftClient.getInstance().keyboard.setClipboard(msaDeviceCode.userCode());
                        } else {
                            MinecraftClient.getInstance().setScreen(prevScreen);
                            Thread.currentThread().interrupt();
                        }
                    }, Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.text", msaDeviceCode.userCode()), Text.translatable("misc.viafabricplus.copy"), Text.translatable("misc.viafabricplus.cancel"))));
                    try {
                        Util.getOperatingSystem().open(new URI(msaDeviceCode.verificationUri()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> Thread.currentThread().interrupt(), Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.error"), Text.translatable("misc.viafabricplus.cancel"), false)));
                    }
                }));
                BedrockAccountHandler.INSTANCE.setAccount(mcChain, MinecraftAuth.BEDROCK_PLAY_FAB_TOKEN.getFromInput(httpClient, mcChain.prevResult().fullXblSession()));
            }
            RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(prevScreen));
        } catch (Throwable e) {
            e.printStackTrace();
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> Thread.currentThread().interrupt(), Text.literal("Microsoft Bedrock login"), Text.translatable("bedrocklogin.viafabricplus.error"), Text.translatable("misc.viafabricplus.cancel"), false)));
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
