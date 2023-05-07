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
package de.florianmichael.viafabricplus.definition.c0_30;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.classic4j.model.classicube.highlevel.CCAccount;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.base.event.DisconnectConnectionCallback;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.base.FileSaver;

import java.util.Map;

public class ClassiCubeAccountHandler extends FileSaver {
    public static ClassiCubeAccountHandler INSTANCE;

    public static void create() {
        ClassiCubeAccountHandler.INSTANCE = new ClassiCubeAccountHandler();
        ClassiCubeAccountHandler.INSTANCE.init();
    }

    private CCAccount account;

    public ClassiCubeAccountHandler() {
        super("classicube.account");

        DisconnectConnectionCallback.EVENT.register(() -> ViaFabricPlusClassicMPPassProvider.classiCubeMPPass = null);
    }

    @Override
    public void write(JsonObject object) {
        if (account == null) return;

        account.token = null; // Token has to be created next time
        for (Map.Entry<String, JsonElement> entry : account.asJson().entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void read(JsonObject object) {
        try {
            account = CCAccount.fromJson(object);
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.warn("No ClassiCube account could be found");
        }
    }

    public CCAccount getAccount() {
        return account;
    }

    public void setAccount(CCAccount account) {
        this.account = account;
    }
}
