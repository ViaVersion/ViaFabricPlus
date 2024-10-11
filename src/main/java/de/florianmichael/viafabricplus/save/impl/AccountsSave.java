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

package de.florianmichael.viafabricplus.save.impl;

import com.google.gson.JsonObject;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.save.AbstractSave;
import de.florianmichael.viafabricplus.settings.impl.BedrockSettings;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession;
import net.raphimc.minecraftauth.step.msa.StepMsaToken;
import net.raphimc.minecraftauth.step.xbl.session.StepInitialXblSession;

public class AccountsSave extends AbstractSave {

    private StepFullBedrockSession.FullBedrockSession bedrockAccount;
    private CCAccount classicubeAccount;

    public AccountsSave() {
        super("accounts");
    }

    @Override
    public void write(JsonObject object) {
        if (bedrockAccount != null) {
            object.add("bedrockV2", BedrockSettings.BEDROCK_DEVICE_CODE_LOGIN.toJson(bedrockAccount));
        }
        if (classicubeAccount != null) {
            object.add("classicube", classicubeAccount.asJson());
        }
    }

    @Override
    public void read(JsonObject object) {
        handleAccount("bedrock", object, account -> {
            // Use old login flow, then get refresh token and login via new flow
            final StepFullBedrockSession.FullBedrockSession oldSession = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.fromJson(account);
            final StepInitialXblSession.InitialXblSession xblSession = oldSession.getMcChain().getXblXsts().getInitialXblSession();

            final StepMsaToken.RefreshToken refreshToken = new StepMsaToken.RefreshToken(xblSession.getMsaToken().getRefreshToken());
            bedrockAccount = BedrockSettings.BEDROCK_DEVICE_CODE_LOGIN.getFromInput(MinecraftAuth.createHttpClient(), refreshToken);
        });
        handleAccount("bedrockV2", object, account -> bedrockAccount = BedrockSettings.BEDROCK_DEVICE_CODE_LOGIN.fromJson(account));
        handleAccount("classicube", object, account -> classicubeAccount = CCAccount.fromJson(account));
    }

    private void handleAccount(final String name, final JsonObject object, final AccountConsumer output) {
        if (object.has(name)) {
            try {
                output.accept(object.get(name).getAsJsonObject());
            } catch (Exception e) {
                ViaFabricPlus.global().getLogger().error("Failed to read {} account!", name, e);
            }
        }
    }

    public StepFullBedrockSession.FullBedrockSession refreshAndGetBedrockAccount() {
        if (bedrockAccount == null) {
            return null;
        }
        try {
            bedrockAccount = BedrockSettings.BEDROCK_DEVICE_CODE_LOGIN.refresh(MinecraftAuth.createHttpClient(), bedrockAccount);
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

    @FunctionalInterface
    interface AccountConsumer {

        void accept(JsonObject account) throws Exception;

    }

}
