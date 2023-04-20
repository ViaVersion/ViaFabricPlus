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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.util.FileSaver;
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.step.bedrock.StepMCChain;
import net.raphimc.mcauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Map;

public class BedrockAccountHandler extends FileSaver {
    public static BedrockAccountHandler INSTANCE;

    public static void create() {
        BedrockAccountHandler.INSTANCE = new BedrockAccountHandler();
        BedrockAccountHandler.INSTANCE.init();
    }

    private StepMCChain.MCChain account;

    public BedrockAccountHandler() {
        super("bedrock.account");
    }

    @Override
    public void write(JsonObject object) {
        if (account == null) return;

        for (Map.Entry<String, JsonElement> entry : account.toJson().entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void read(JsonObject object) {
        try {
            account = MinecraftAuth.Bedrock.Title.MC_CHAIN.fromJson(object);
            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                account = MinecraftAuth.Bedrock.Title.MC_CHAIN.refresh(httpClient, account);
            }
        } catch (Exception e) {
            if (System.getProperty("VFPDebug") != null) {
                ViaFabricPlus.LOGGER.error("Failed to log into Bedrock account!", e);
            } else {
                ViaFabricPlus.LOGGER.error("Failed to log into Bedrock account! Use -DVFPDebug to show the log");
            }
        }
    }

    public StepMCChain.MCChain getAccount() {
        return account;
    }

    public void setAccount(StepMCChain.MCChain account) {
        this.account = account;
    }
}
