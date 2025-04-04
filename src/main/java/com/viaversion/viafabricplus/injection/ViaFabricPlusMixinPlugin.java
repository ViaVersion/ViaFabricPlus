/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection;

import com.viaversion.viafabricplus.features.movement.elytra.FabricAPIWorkaround;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class ViaFabricPlusMixinPlugin implements IMixinConfigPlugin {

    private static final String MIXINS_PACKAGE = "com.viaversion.viafabricplus.injection.mixin.";

    public static boolean IPNEXT_PRESENT;
    public static boolean MORE_CULLING_PRESENT;
    public static boolean LITHIUM_PRESENT;
    public static boolean MOONRISE_PRESENT;
    public static boolean LEGENDARYTOOLTIPS_PRESENT;
    public static boolean LEGACY_PRESENT;
    public static boolean LAMBDYNLIGHTS_PRESENT;

    @Override
    public void onLoad(String mixinPackage) {
        final FabricLoader loader = FabricLoader.getInstance();
        IPNEXT_PRESENT = loader.isModLoaded("inventoryprofilesnext");
        MORE_CULLING_PRESENT = loader.isModLoaded("moreculling");
        LITHIUM_PRESENT = loader.isModLoaded("lithium");
        MOONRISE_PRESENT = loader.isModLoaded("moonrise");
        LEGENDARYTOOLTIPS_PRESENT = loader.isModLoaded("legendarytooltips");
        LEGACY_PRESENT = loader.isModLoaded("legacy");
        LAMBDYNLIGHTS_PRESENT = loader.isModLoaded("lambdynlights");

        FabricAPIWorkaround.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return switch (mixinClassName) {
            case MIXINS_PACKAGE + "compat.ipnext.MixinAutoRefillHandler_ItemSlotMonitor" -> IPNEXT_PRESENT;
            case MIXINS_PACKAGE + "compat.lithium.MixinEntity" -> LITHIUM_PRESENT && !MOONRISE_PRESENT;
            case MIXINS_PACKAGE + "features.item.attack_damage.MixinItemStack" -> !LEGENDARYTOOLTIPS_PRESENT;
            case MIXINS_PACKAGE + "features.item.negative_item_count.MixinDrawContext" -> !LEGACY_PRESENT;
            case MIXINS_PACKAGE + "features.world.always_tick_entities.MixinClientWorld" -> !LAMBDYNLIGHTS_PRESENT;
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}
