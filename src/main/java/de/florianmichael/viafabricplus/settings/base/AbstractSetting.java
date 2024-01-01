/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.settings.base;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.screen.VFPListEntry;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * This class is the base for all settings. It contains the name, the default value and the current value.
 * Developer's should always use the implementations of this class, and not this class itself.
 *
 * @param <T> The type of the setting.
 */
public abstract class AbstractSetting<T> {
    private final MutableText name;
    private final T defaultValue;

    private T value;

    private Text tooltip;

    public AbstractSetting(final SettingGroup parent, final MutableText name, final T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;

        this.value = defaultValue;

        parent.getSettings().add(this);
    }

    /**
     * This method is used to create a renderer for the setting.
     * @return The renderer, see {@link VFPListEntry} for more details.
     */
    public abstract VFPListEntry makeSettingRenderer();

    public abstract void write(final JsonObject object);
    public abstract void read(final JsonObject object);

    public MutableText getName() {
        return name;
    }

    /**
     * @return The translation key of the name.
     */
    public String getTranslationKey() {
        return mapTranslationKey(ChatUtil.uncoverTranslationKey(name));
    }

    /**
     * Cuts the name of a translation from its key / path.
     *
     * @param input The translation key.
     * @return The name of the translation. (E. g: "viafabricplus.settings.base" -> "base")
     */
    public static String mapTranslationKey(final String input) {
        return input.split("viafabricplus.")[1];
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Text getTooltip() {
        return tooltip;
    }

    public void setTooltip(Text tooltip) {
        this.tooltip = tooltip;
    }

}
