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
package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import net.minecraft.text.Text;

public enum ClassiCubeError {
    TOKEN(Text.translatable("classicube.viafabricplus.error.token")),
    USERNAME(Text.translatable("classicube.viafabricplus.error.username")),
    PASSWORD(Text.translatable("classicube.viafabricplus.error.password")),
    VERIFICATION(Text.translatable("classicube.viafabricplus.error.verification"), false),
    LOGIN_CODE(Text.translatable("classicube.viafabricplus.error.logincode"));

    public final Text description;
    public final boolean fatal;

    ClassiCubeError(Text description) {
        this(description, true);
    }

    ClassiCubeError(Text description, boolean fatal) {
        this.description = description;
        this.fatal = fatal;
    }
}
