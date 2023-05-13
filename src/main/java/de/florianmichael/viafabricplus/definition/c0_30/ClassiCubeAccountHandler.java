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

    private String username;
    private String password;

    public ClassiCubeAccountHandler() {
        super("classicube.account");

        DisconnectConnectionCallback.EVENT.register(() -> ViaFabricPlusClassicMPPassProvider.classiCubeMPPass = null);
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty("username", username);
        object.addProperty("password", password);
    }

    @Override
    public void read(JsonObject object) {
        if (object.has("username")) username = object.get("username").getAsString();
        if (object.has("password")) password = object.get("password").getAsString();
    }

    public CCAccount getAccount() {
        return account;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(CCAccount account) {
        this.account = account;
        if (account != null) {
            username = account.username();
            password = account.password();
        }
    }
}
