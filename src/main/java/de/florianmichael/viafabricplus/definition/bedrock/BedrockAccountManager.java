/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.step.bedrock.StepMCChain;
import net.raphimc.mcauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.*;

public class BedrockAccountManager {
    public final static BedrockAccountManager INSTANCE = new BedrockAccountManager();

    private final File ACCOUNT_FILE = new File(ViaFabricPlus.RUN_DIRECTORY, "bedrock.account");
    private StepMCChain.MCChain account;

    public void load() {
        if (ACCOUNT_FILE.exists()) {
            final JsonObject mcChain;
            try {
                mcChain = ViaFabricPlus.GSON.fromJson(new FileReader(ACCOUNT_FILE), JsonObject.class).getAsJsonObject();
                account = MinecraftAuth.Bedrock.Title.MC_CHAIN.fromJson(mcChain);

                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    account = MinecraftAuth.Bedrock.Title.MC_CHAIN.refresh(httpClient, account);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
    }

    public void save() {
        if (account != null) {
            ACCOUNT_FILE.delete();
            try {
                ACCOUNT_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (final FileWriter fw = new FileWriter(ACCOUNT_FILE)) {
                fw.write(ViaFabricPlus.GSON.toJson(account.toJson()));
                fw.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
