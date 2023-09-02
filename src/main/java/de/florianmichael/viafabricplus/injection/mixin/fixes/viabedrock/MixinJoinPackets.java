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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viabedrock;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viafabricplus.definition.tracker.bedrock.JoinGameTracker;
import net.raphimc.viabedrock.protocol.packets.JoinPackets;
import net.raphimc.viabedrock.protocol.types.primitive.LongLEType;
import net.raphimc.viabedrock.protocol.types.primitive.StringType;
import net.raphimc.viabedrock.protocol.types.primitive.VarIntType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JoinPackets.class, remap = false)
public class MixinJoinPackets {

    @Redirect(method = "lambda$register$2", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;read(Lcom/viaversion/viaversion/api/type/Type;)Ljava/lang/Object;", ordinal = 5))
    private static Object trackWorldSeed(PacketWrapper instance, Type<LongLEType> tType) throws Exception {
        final Object seed = instance.read(tType);
        instance.user().get(JoinGameTracker.class).setSeed((long) seed);
        return seed;
    }

    @Redirect(method = "lambda$register$2", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;read(Lcom/viaversion/viaversion/api/type/Type;)Ljava/lang/Object;", ordinal = 56))
    private static Object trackLevelId(PacketWrapper instance, Type<StringType> tType) throws Exception {
        final Object levelId = instance.read(tType);
        instance.user().get(JoinGameTracker.class).setLevelId((String) levelId);
        return levelId;
    }

    @Redirect(method = "lambda$register$2", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;read(Lcom/viaversion/viaversion/api/type/Type;)Ljava/lang/Object;", ordinal = 63))
    private static Object trackEnchantmentSeed(PacketWrapper instance, Type<VarIntType> tType) throws Exception {
        final Object enchantmentSeed = instance.read(tType);
        instance.user().get(JoinGameTracker.class).setEnchantmentSeed((Integer) enchantmentSeed);
        return enchantmentSeed;
    }
}
