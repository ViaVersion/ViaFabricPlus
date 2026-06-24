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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.allow_new_line;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Font.class)
public abstract class MixinFont {

    @Shadow
    public abstract List<FormattedCharSequence> split(FormattedText text, int width);

    @Shadow
    public abstract String bidirectionalShaping(String text);

    @Shadow
    @Final
    public int lineHeight;

    @Shadow
    public abstract int width(FormattedCharSequence text);

    @Shadow
    public abstract boolean isBidirectional();

    @Shadow
    public abstract Font.PreparedText prepareText(final FormattedCharSequence text, final float x, final float y, final int originalColor, final boolean drawShadow, final boolean includeEmpty, final int backgroundColor);

    // TODO 26.2
//    @Inject(method = "prepareText(Ljava/lang/String;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;", at = @At("HEAD"), cancellable = true)
//    private void allowNewLines_String(String text, float x, float y, int originalColor, boolean drawShadow, int backgroundColor, CallbackInfoReturnable<Font.PreparedText> cir) {
//        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
//            final List<FormattedCharSequence> lines = split(FormattedText.of(isBidirectional() ? this.bidirectionalShaping(text) : text), Integer.MAX_VALUE);
//            if (!lines.isEmpty()) {
//                ci.cancel();
//                for (int i = 0, size = lines.size(); i < size; i++) {
//                    this.prepareText(lines.get(i), x, y - (size * (lineHeight + 2)) + (i * (lineHeight + 2)), originalColor, drawShadow, false, backgroundColor);
//                }
//            }
//        }
//    }
//
//    @Inject(method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;", at = @At("HEAD"), cancellable = true)
//    private void allowNewLines_Text(FormattedCharSequence text, float x, float y, int originalColor, boolean drawShadow, boolean includeEmpty, int backgroundColor, CallbackInfoReturnable<Font.PreparedText> cir) {
//        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
//            final List<FormattedCharSequence> lines = split(str, Integer.MAX_VALUE);
//            if (!lines.isEmpty()) {
//                ci.cancel();
//                for (int i = 0, size = lines.size(); i < size; i++) {
//                    this.drawInBatch(lines.get(i), x, y - (lines.size() * (lineHeight + 2)) + (i * (lineHeight + 2)), color, dropShadow, new Matrix4f(pose), bufferSource, displayMode, backgroundColor, packedLightCoords);
//                }
//            }
//        }
//    }
//
//    @Inject(method = "width(Lnet/minecraft/network/chat/FormattedText;)I", at = @At("HEAD"), cancellable = true)
//    private void allowNewLines_getWidth(FormattedText text, CallbackInfoReturnable<Integer> cir) {
//        if (Minecraft.getInstance().level != null && ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
//            int i = 0;
//            for (FormattedCharSequence wrapLine : this.split(text, Integer.MAX_VALUE)) {
//                if (width(wrapLine) >= i) i = width(wrapLine);
//            }
//            cir.setReturnValue(Mth.ceil(i));
//        }
//    }

}
