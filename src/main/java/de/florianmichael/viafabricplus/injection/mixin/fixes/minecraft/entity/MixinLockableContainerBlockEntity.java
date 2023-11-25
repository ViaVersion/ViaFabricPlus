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


package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LockableContainerBlockEntity.class)
public class MixinLockableContainerBlockEntity {

    /*
    Workaround for https://github.com/ViaVersion/ViaFabricPlus/issues/223

    TODO | The problem:
    ViaVersion rewrites components in all protocols not really well, which also leads to some other issues
    (see to-do list and https://github.com/ViaVersion/ViaVersion/issues/3413), some servers send custom names as json
    which the old 1.12.2 client could read, but modern versions can't anymore, we have to implement the component rewriting in ViaVersion completely for all
    protocols so that this issue can be fixed properly, but since nobody will probably do that in the near future,
    this fix, so that you can at least see / join the world.
     */

    @WrapOperation(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text$Serializer;fromJson(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    public MutableText allowInvalidJson(String json, Operation<MutableText> operation) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            try {
                return operation.call(json);
            } catch (Exception e) { // In case the json is invalid for the modern client, we just return the raw json
                return Text.literal(json);
            }
        }

        return operation.call(json);
    }
}
