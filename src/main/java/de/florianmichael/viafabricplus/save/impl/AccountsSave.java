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

package de.florianmichael.viafabricplus.save.impl;

import com.google.gson.JsonObject;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.save.AbstractSave;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class AccountsSave extends AbstractSave {

    private StepFullBedrockSession.FullBedrockSession bedrockAccount;
    private CCAccount classicubeAccount;

    public AccountsSave() {
        super("accounts");
    }

    @Override
    public void write(JsonObject object) {
        if (bedrockAccount != null) {
            object.add("bedrock", MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.toJson(bedrockAccount));
        }
        if (classicubeAccount != null) {
            object.add("classicube", classicubeAccount.asJson());
        }
    }

    @Override
    public void read(JsonObject object) {
        if (object.has("bedrock")) {
            try {
                bedrockAccount = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.fromJson(object.get("bedrock").getAsJsonObject());
            } catch (Exception e) {
                ViaFabricPlus.global().getLogger().error("Failed to read bedrock account!");
            }
        }
        if (object.has("classicube")) {
            try {
                classicubeAccount = CCAccount.fromJson(object.get("classicube").getAsJsonObject());
            } catch (Exception e) {
                ViaFabricPlus.global().getLogger().error("Failed to read classicube account!");
            }
        }
    }

    public StepFullBedrockSession.FullBedrockSession refreshAndGetBedrockAccount() {
        if (bedrockAccount == null) return null;

        try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
            bedrockAccount = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.refresh(httpClient, bedrockAccount);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to refresh Bedrock chain data. Please re-login to Bedrock!", t);
        }

        return bedrockAccount;
    }

    public StepFullBedrockSession.FullBedrockSession getBedrockAccount() {
        return bedrockAccount;
    }

    public void setBedrockAccount(StepFullBedrockSession.FullBedrockSession bedrockAccount) {
        this.bedrockAccount = bedrockAccount;
    }

    public CCAccount getClassicubeAccount() {
        return classicubeAccount;
    }

    public void setClassicubeAccount(CCAccount classicubeAccount) {
        this.classicubeAccount = classicubeAccount;
    }
}
