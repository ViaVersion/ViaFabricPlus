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
package de.florianmichael.viafabricplus.definition.bedrock;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.base.file.FileSaver;
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.step.bedrock.StepMCChain;
import net.raphimc.mcauth.step.bedrock.StepPlayFabToken;
import net.raphimc.mcauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class BedrockAccountHandler extends FileSaver {
    public static BedrockAccountHandler INSTANCE;

    public static void create() {
        BedrockAccountHandler.INSTANCE = new BedrockAccountHandler();
        BedrockAccountHandler.INSTANCE.init();
    }

    private StepMCChain.MCChain mcChain;
    private StepPlayFabToken.PlayFabToken playFabToken;

    public BedrockAccountHandler() {
        super("bedrock.account");
    }

    @Override
    public void write(JsonObject object) {
        if (mcChain == null) return;

        object.add("mc-chain", mcChain.toJson());
        object.add("play-fab-token", playFabToken.toJson());
    }

    @Override
    public void read(JsonObject object) {
        try {
            mcChain = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.fromJson(object.get("mc-chain").getAsJsonObject());
            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                mcChain = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.refresh(httpClient, mcChain);
            }

            playFabToken = MinecraftAuth.BEDROCK_PLAY_FAB_TOKEN.fromJson(object.get("play-fab-token").getAsJsonObject());
            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                playFabToken = MinecraftAuth.BEDROCK_PLAY_FAB_TOKEN.refresh(httpClient, playFabToken);
            }
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.warn("No Bedrock account could be found");
        }
    }

    public void setAccount(final StepMCChain.MCChain mcChain, final StepPlayFabToken.PlayFabToken playFabToken) {
        this.mcChain = mcChain;
        this.playFabToken = playFabToken;
    }

    public StepMCChain.MCChain getMcChain() {
        return mcChain;
    }

    public StepPlayFabToken.PlayFabToken getPlayFabToken() {
        return playFabToken;
    }
}
