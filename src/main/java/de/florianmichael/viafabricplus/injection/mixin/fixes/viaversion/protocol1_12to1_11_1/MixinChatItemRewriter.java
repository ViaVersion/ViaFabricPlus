/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2023 RK_01/RaphiMC and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_12to1_11_1;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ChatItemRewriter;
import net.lenni0451.mcstructs.snbt.SNbtSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = ChatItemRewriter.class, remap = false)
public abstract class MixinChatItemRewriter {

    @Redirect(method = "toClient", at = @At(value = "INVOKE", target = "Ljava/util/regex/Pattern;matcher(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;"))
    private static Matcher rewriteShowItem(Pattern pattern, CharSequence input) {
        try {
            input = SNbtSerializer.V1_12.serialize(SNbtSerializer.V1_8.deserialize(input.toString()));
        } catch (Throwable e) {
            Via.getPlatform().getLogger().log(Level.WARNING, "Error converting 1.11.2 nbt to 1.12.2 nbt: '" + input + "'", e);
        }
        return Pattern.compile("$^").matcher(input);
    }
}
