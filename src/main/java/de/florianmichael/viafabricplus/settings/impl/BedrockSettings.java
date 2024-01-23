/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.settings.base.BooleanSetting;
import de.florianmichael.viafabricplus.settings.base.ButtonSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class BedrockSettings extends SettingGroup {

    private static final BedrockSettings instance = new BedrockSettings();

    public final ButtonSetting _1 = new ButtonSetting(this, Text.translatable("bedrock_settings.viafabricplus.click_to_set_bedrock_account"), () -> CompletableFuture.runAsync(this::openBedrockAccountLogin)) {
        
        @Override
        public MutableText displayValue() {
            final var account = ViaFabricPlus.global().getSaveManager().getAccountsSave().getBedrockAccount();
            if (account == null) return super.displayValue();

            return Text.literal("Bedrock account: " + account.getMcChain().getDisplayName());
        }
    };
    public final BooleanSetting openPromptGUIToConfirmTransfer = new BooleanSetting(this, Text.translatable("bedrock_settings.viafabricplus.confirm_transfer_server_prompt"), true);

    public BedrockSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.bedrock"));
    }
    
    private void openBedrockAccountLogin() {
        final var prevScreen = MinecraftClient.getInstance().currentScreen;
        try {
            ViaFabricPlus.global().getSaveManager().getAccountsSave().setBedrockAccount(MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.getFromInput(MinecraftAuth.createHttpClient(), new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> {
                    MinecraftClient.getInstance().setScreen(prevScreen);
                    Thread.currentThread().interrupt();
                }, Text.literal("Microsoft Bedrock login"), Text.translatable("bedrock.viafabricplus.login"), Text.translatable("base.viafabricplus.cancel"), true)));
                try {
                    Util.getOperatingSystem().open(new URI(msaDeviceCode.getDirectVerificationUri()));
                } catch (URISyntaxException e) {
                    Thread.currentThread().interrupt();
                    VFPScreen.showErrorScreen("Microsoft Bedrock Login", e, prevScreen);
                }
            })));

            RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(prevScreen));
        } catch (Throwable e) {
            Thread.currentThread().interrupt();
            VFPScreen.showErrorScreen("Microsoft Bedrock Login", e, prevScreen);
        }
    }

    public static BedrockSettings global() {
        return instance;
    }

}
