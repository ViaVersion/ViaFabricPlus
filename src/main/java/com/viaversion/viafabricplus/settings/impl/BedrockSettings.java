/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.bedrock.BedrockAuthManager;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;
import net.raphimc.minecraftauth.util.holder.listener.ChangeListener;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;

public final class BedrockSettings extends SettingGroup {

    private static final Component TITLE = Component.nullToEmpty("Microsoft Bedrock login");

    public static final BedrockSettings INSTANCE = new BedrockSettings();

    private Thread thread;
    private final ButtonSetting clickToSetBedrockAccount = new ButtonSetting(this, Component.translatable("bedrock_settings.viafabricplus.click_to_set_bedrock_account"), () -> {
        thread = new Thread(this::openBedrockAccountLogin);
        thread.start();
    }) {

        @Override
        public MutableComponent displayValue() {
            final BedrockAuthManager account = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount();
            if (account != null && account.getMinecraftMultiplayerToken().hasValue()) {
                return Component.translatable("click_to_set_bedrock_account.viafabricplus.display", account.getMinecraftMultiplayerToken().getCached().getDisplayName());
            } else {
                return super.displayValue();
            }
        }
    };
    public final BooleanSetting replaceDefaultPort = new BooleanSetting(this, Component.translatable("bedrock_settings.viafabricplus.replace_default_port"), true);
    public final BooleanSetting experimentalFeatures = new BooleanSetting(this, Component.translatable("bedrock_settings.viafabricplus.experimental_features"), true);

    public BedrockSettings() {
        super(Component.translatable("setting_group_name.viafabricplus.bedrock"));
    }

    private void openBedrockAccountLogin() {
        final AccountsSave accountsSave = SaveManager.INSTANCE.getAccountsSave();

        final Minecraft client = Minecraft.getInstance();
        final Screen prevScreen = client.screen;
        try {
            final BedrockAuthManager bedrockAccount = BedrockAuthManager
                .create(MinecraftAuth.createHttpClient(), ProtocolConstants.BEDROCK_VERSION_NAME)
                .login(DeviceCodeMsaAuthService::new, (Consumer<MsaDeviceCode>) msaDeviceCode -> {
                    VFPScreen.setScreen(new ConfirmScreen(copyUrl -> {
                        if (copyUrl) {
                            client.keyboardHandler.setClipboard(msaDeviceCode.getDirectVerificationUri());
                        } else {
                            client.setScreen(prevScreen);
                            this.thread.interrupt();
                        }
                    }, TITLE, Component.translatable("click_to_set_bedrock_account.viafabricplus.notice"), Component.translatable("base.viafabricplus.copy_link"), Component.translatable("base.viafabricplus.cancel")));
                    Util.getPlatform().openUri(msaDeviceCode.getDirectVerificationUri());
                });
            bedrockAccount.getChangeListeners().add(new ChangeListener() {
                @Override
                public <T> void onChange(final T oldValue, final T newValue) {
                    if (newValue == bedrockAccount.getMsaToken().getCached()) {
                        updateLoginStatusMessage("msatoken");
                    } else if (newValue == bedrockAccount.getXblDeviceToken().getCached()) {
                        updateLoginStatusMessage("xbldevicetoken");
                    } else if (newValue == bedrockAccount.getXblUserToken().getCached()) {
                        updateLoginStatusMessage("xblusertoken");
                    } else if (newValue == bedrockAccount.getXblTitleToken().getCached()) {
                        updateLoginStatusMessage("xbltitletoken");
                    } else if (newValue == bedrockAccount.getBedrockXstsToken().getCached()) {
                        updateLoginStatusMessage("bedrockxststoken");
                    } else if (newValue == bedrockAccount.getPlayFabXstsToken().getCached()) {
                        updateLoginStatusMessage("playfabxststoken");
                    } else if (newValue == bedrockAccount.getRealmsXstsToken().getCached()) {
                        updateLoginStatusMessage("realmsxststoken");
                    } else if (newValue == bedrockAccount.getPlayFabToken().getCached()) {
                        updateLoginStatusMessage("playfabtoken");
                    } else if (newValue == bedrockAccount.getMinecraftSession().getCached()) {
                        updateLoginStatusMessage("minecraftsession");
                    } else if (newValue == bedrockAccount.getMinecraftMultiplayerToken().getCached()) {
                        updateLoginStatusMessage("minecraftmultiplayertoken");
                    } else if (newValue == bedrockAccount.getMinecraftCertificateChain().getCached()) {
                        updateLoginStatusMessage("minecraftcertificatechain");
                    }
                }
            });
            bedrockAccount.getMinecraftMultiplayerToken().refreshIfExpired();
            bedrockAccount.getMinecraftCertificateChain().refreshIfExpired();
            accountsSave.setBedrockAccount(bedrockAccount);

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

    private static void updateLoginStatusMessage(final String stepName) {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().screen instanceof ConfirmScreen confirmScreen) {
                ((IConfirmScreen) confirmScreen).viaFabricPlus$updateMessage(Component.translatable("minecraftauth_library.viafabricplus." + stepName));
            }
        });
    }

}
