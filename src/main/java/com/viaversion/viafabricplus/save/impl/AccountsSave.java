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

package com.viaversion.viafabricplus.save.impl;

import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.save.AbstractSave;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.bedrock.BedrockAuthManager;
import net.raphimc.minecraftauth.util.MinecraftAuth4To5Migrator;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;

public final class AccountsSave extends AbstractSave {

    private BedrockAuthManager bedrockAccount;
    private CCAccount classicubeAccount;

    public AccountsSave() {
        super("accounts");
    }

    @Override
    public void write(JsonObject object) {
        if (bedrockAccount != null) {
            object.add("bedrockV3", BedrockAuthManager.toJson(bedrockAccount));
        }
        if (classicubeAccount != null) {
            object.add("classicube", classicubeAccount.asJson());
        }
    }

    @Override
    public void read(JsonObject object) {
        handleAccount("bedrockV2", object, account -> {
            final JsonObject newAccount = MinecraftAuth4To5Migrator.migrateBedrockSave(account);
            bedrockAccount = BedrockAuthManager.fromJson(MinecraftAuth.createHttpClient(), ProtocolConstants.BEDROCK_VERSION_NAME, newAccount);
            bedrockAccount.getMinecraftMultiplayerToken().refreshIfExpired();
        });
        handleAccount("bedrockV3", object, account -> bedrockAccount = BedrockAuthManager.fromJson(MinecraftAuth.createHttpClient(), ProtocolConstants.BEDROCK_VERSION_NAME, account));
        handleAccount("classicube", object, account -> classicubeAccount = CCAccount.fromJson(account));
    }

    private void handleAccount(final String name, final JsonObject object, final AccountConsumer output) {
        if (object.has(name)) {
            try {
                output.accept(object.get(name).getAsJsonObject());
            } catch (Exception e) {
                ViaFabricPlusImpl.INSTANCE.getLogger().error("Failed to read {} account!", name, e);
            }
        }
    }

    public BedrockAuthManager getBedrockAccount() {
        return bedrockAccount;
    }

    public void setBedrockAccount(BedrockAuthManager bedrockAccount) {
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
