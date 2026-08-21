/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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
import de.florianreuth.classic4j.model.classicube.account.CCAccount;

public final class AccountsSave extends AbstractSave {

    private CCAccount classicubeAccount;

    public AccountsSave() {
        super("accounts");
    }

    @Override
    public void write(JsonObject object) {
        if (classicubeAccount != null) {
            object.add("classicube", classicubeAccount.asJson());
        }
    }

    @Override
    public void read(JsonObject object) {
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
