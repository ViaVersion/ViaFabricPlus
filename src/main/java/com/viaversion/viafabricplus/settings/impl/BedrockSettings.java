/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.settings.impl;

import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.type.ButtonSetting;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IConfirmScreen;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.save.impl.AccountsSave;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCodeMsaCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import net.raphimc.minecraftauth.util.logging.ConsoleLogger;
import net.raphimc.minecraftauth.util.logging.ILogger;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;

import java.util.Locale;
import java.util.Objects;

public final class BedrockSettings extends SettingGroup {

    private static final Text TITLE = Text.of("Microsoft Bedrock login");

    public static final BedrockSettings INSTANCE = new BedrockSettings();

    public static final AbstractStep<?, StepFullBedrockSession.FullBedrockSession> BEDROCK_DEVICE_CODE_LOGIN = MinecraftAuth.builder()
            .withClientId(MicrosoftConstants.BEDROCK_ANDROID_TITLE_ID).withScope(MicrosoftConstants.SCOPE_TITLE_AUTH)
            .deviceCode()
            .withDeviceToken("Android")
            .sisuTitleAuthentication(MicrosoftConstants.BEDROCK_XSTS_RELYING_PARTY)
            .buildMinecraftBedrockChainStep(true, true);

    private Thread thread;
    private final ButtonSetting clickToSetBedrockAccount = new ButtonSetting(this, Text.translatable("bedrock_settings.viafabricplus.click_to_set_bedrock_account"), () -> {
        thread = new Thread(this::openBedrockAccountLogin);
        thread.start();
    }) {

        @Override
        public MutableText displayValue() {
            final StepFullBedrockSession.FullBedrockSession account = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount();
            if (account != null) {
                return Text.translatable("click_to_set_bedrock_account.viafabricplus.display", account.getMcChain().getDisplayName());
            } else {
                return super.displayValue();
            }
        }
    };
    public final BooleanSetting replaceDefaultPort = new BooleanSetting(this, Text.translatable("bedrock_settings.viafabricplus.replace_default_port"), true);

    private final ILogger GUI_LOGGER = new ConsoleLogger() {
        @Override
        public void info(AbstractStep<?, ?> step, String message) {
            super.info(step, message);
            if (step instanceof StepMsaDeviceCodeMsaCode) {
                return;
            }
            MinecraftClient.getInstance().execute(() -> {
                if (MinecraftClient.getInstance().currentScreen instanceof ConfirmScreen confirmScreen) {
                    ((IConfirmScreen) confirmScreen).viaFabricPlus$setMessage(Text.translatable("minecraftauth_library.viafabricplus." + step.name.toLowerCase(Locale.ROOT)));
                }
            });
        }
    };

    public BedrockSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.bedrock"));
    }

    private void openBedrockAccountLogin() {
        final AccountsSave accountsSave = SaveManager.INSTANCE.getAccountsSave();

        final MinecraftClient client = MinecraftClient.getInstance();
        final Screen prevScreen = client.currentScreen;
        try {
            accountsSave.setBedrockAccount(BEDROCK_DEVICE_CODE_LOGIN.getFromInput(GUI_LOGGER, MinecraftAuth.createHttpClient(), new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                VFPScreen.setScreen(new ConfirmScreen(copyUrl -> {
                    if (copyUrl) {
                        client.keyboard.setClipboard(msaDeviceCode.getDirectVerificationUri());
                    } else {
                        client.setScreen(prevScreen);
                        this.thread.interrupt();
                    }
                }, TITLE, Text.translatable("click_to_set_bedrock_account.viafabricplus.notice"), Text.translatable("base.viafabricplus.copy_link"), Text.translatable("base.viafabricplus.cancel")));
                Util.getOperatingSystem().open(msaDeviceCode.getDirectVerificationUri());
            })));

            VFPScreen.setScreen(prevScreen);
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                return;
            }
            this.thread.interrupt();
            VFPScreen.showErrorScreen(TITLE, e, prevScreen);
        }
    }

    /**
     * Replaces the default port when parsing a server address if the default port should be replaced
     *
     * @param address The original address of the server
     * @param version The protocol version
     * @return The server address with the replaced default port
     */
    public static String replaceDefaultPort(final String address, final ProtocolVersion version) {
        // If the default port for this entry should be replaced, check if the address already contains a port
        // We can't just replace vanilla's default port because a bedrock server might be running on the same port
        if (BedrockSettings.INSTANCE.replaceDefaultPort.getValue() && Objects.equals(version, BedrockProtocolVersion.bedrockLatest) && !address.contains(":")) {
            return address + ":" + ProtocolConstants.BEDROCK_DEFAULT_PORT;
        } else {
            return address;
        }
    }

}
