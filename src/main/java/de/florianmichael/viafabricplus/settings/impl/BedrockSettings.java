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

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IConfirmScreen;
import de.florianmichael.viafabricplus.save.impl.AccountsSave;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.settings.base.BooleanSetting;
import de.florianmichael.viafabricplus.settings.base.ButtonSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
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

import java.util.Locale;

public class BedrockSettings extends SettingGroup {

    private static final Text TITLE = Text.of("Microsoft Bedrock login");

    private static final BedrockSettings INSTANCE = new BedrockSettings();

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
            final StepFullBedrockSession.FullBedrockSession account = ViaFabricPlus.global().getSaveManager().getAccountsSave().getBedrockAccount();
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
        final AccountsSave accountsSave = ViaFabricPlus.global().getSaveManager().getAccountsSave();

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

    public static BedrockSettings global() {
        return INSTANCE;
    }

}
