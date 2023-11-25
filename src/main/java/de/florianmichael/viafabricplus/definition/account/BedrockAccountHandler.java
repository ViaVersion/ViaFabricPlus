/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.definition.account;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.util.FileSaver;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class BedrockAccountHandler extends FileSaver {
    public static BedrockAccountHandler INSTANCE;

    public static void create() {
        BedrockAccountHandler.INSTANCE = new BedrockAccountHandler();
        BedrockAccountHandler.INSTANCE.init();
    }

    private StepFullBedrockSession.FullBedrockSession bedrockSession;

    public BedrockAccountHandler() {
        super("bedrock.account");
    }

    @Override
    public void write(JsonObject object) {
        if (bedrockSession == null) return;

        object.add("bedrockSession", MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.toJson(bedrockSession));
    }

    @Override
    public void read(JsonObject object) {
        try {
            bedrockSession = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.fromJson(object.get("bedrockSession").getAsJsonObject());

            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                bedrockSession = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.refresh(httpClient, bedrockSession);
            }
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.warn("No Bedrock account could be found");
        }
    }

    public StepFullBedrockSession.FullBedrockSession getBedrockSession() {
        return bedrockSession;
    }

    public void setBedrockSession(StepFullBedrockSession.FullBedrockSession bedrockSession) {
        this.bedrockSession = bedrockSession;
    }
}
