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

package de.florianmichael.viafabricplus.injection;

import net.fabricmc.loader.api.FabricLoader;
import net.lenni0451.reflect.stream.RStream;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ViaFabricPlusMixinPlugin implements IMixinConfigPlugin {

    public static final String INJECTOR_PACKAGE = "de.florianmichael.viafabricplus.injection.mixin.";

    private static final String MC_FIXES_PACKAGE = "fixes.minecraft.";
    private static final String COMPAT_PACKAGE = "compat.";

    public static String VFP_VERSION;

    public static boolean DASH_LOADER_PRESENT;
    public static boolean ARMOR_SKIN_PRESENT;
    public static boolean IPNEXT_PRESENT;

    @Override
    public void onLoad(String mixinPackage) {
        final FabricLoader loader = FabricLoader.getInstance();

        VFP_VERSION = loader.getModContainer("viafabricplus").get().getMetadata().getVersion().getFriendlyString();

        DASH_LOADER_PRESENT = loader.isModLoaded("dashloader");
        ARMOR_SKIN_PRESENT = loader.isModLoaded("armorskin");
        IPNEXT_PRESENT = loader.isModLoaded("inventoryprofilesnext");

        // Force unload some FabricAPI mixins because FabricAPI overwrites some of the elytra code
        final Set<String> loadedMixins = RStream.of("org.spongepowered.asm.mixin.transformer.MixinConfig").fields().by("globalMixinList").get();
        loadedMixins.add("net.fabricmc.fabric.mixin.client.entity.event.elytra.ClientPlayerEntityMixin");
        loadedMixins.add("net.fabricmc.fabric.mixin.entity.event.elytra.LivingEntityMixin");
        loadedMixins.add("net.fabricmc.fabric.mixin.entity.event.elytra.PlayerEntityMixin");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals(INJECTOR_PACKAGE + MC_FIXES_PACKAGE + "MixinFontStorage")) {
            return !DASH_LOADER_PRESENT;
        }
        if (mixinClassName.equals(INJECTOR_PACKAGE + MC_FIXES_PACKAGE + "MixinInGameHud")) {
            return !ARMOR_SKIN_PRESENT;
        }
        if (mixinClassName.equals(INJECTOR_PACKAGE + COMPAT_PACKAGE + "ipnext.MixinAutoRefillHandler_ItemSlotMonitor")) {
            return IPNEXT_PRESENT;
        }

        return true;
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
