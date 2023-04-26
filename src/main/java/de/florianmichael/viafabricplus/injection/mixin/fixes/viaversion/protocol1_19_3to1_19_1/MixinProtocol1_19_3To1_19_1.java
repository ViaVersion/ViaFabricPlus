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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_19_3to1_19_1;

import com.google.common.primitives.Longs;
import com.viaversion.viabackwards.protocol.protocol1_19_1to1_19_3.storage.NonceStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.BitSetType;
import com.viaversion.viaversion.api.type.types.ByteArrayType;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viafabricplus.definition.v1_19_2.model.MessageMetadataModel;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import de.florianmichael.viafabricplus.definition.v1_19_2.storage.ChatSession1_19_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = Protocol1_19_3To1_19_1.class, remap = false)
public class MixinProtocol1_19_3To1_19_1 extends AbstractProtocol<ClientboundPackets1_19_1, ClientboundPackets1_19_3, ServerboundPackets1_19_1, ServerboundPackets1_19_3> {

    @Unique
    private final static ByteArrayType.OptionalByteArrayType OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType.OptionalByteArrayType(256);

    @Unique
    private final static ByteArrayType MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType(256);

    @Unique
    private final static BitSetType ACKNOWLEDGED_BIT_SET_TYPE = new BitSetType(20);

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixKeys(CallbackInfo ci) {
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Server-ID
                map(Type.BYTE_ARRAY_PRIMITIVE); // Public Key
                map(Type.BYTE_ARRAY_PRIMITIVE); // Nonce
                handler(wrapper -> wrapper.user().put(new NonceStorage(wrapper.get(Type.BYTE_ARRAY_PRIMITIVE, 1))));
            }
        }, true);
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Name
                handler(wrapper -> {
                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    wrapper.write(Type.OPTIONAL_PROFILE_KEY, chatSession1192 != null ? chatSession1192.getProfileKey() : null); // Profile Key
                });
                map(Type.OPTIONAL_UUID); // UUID
            }
        }, true);

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE_ARRAY_PRIMITIVE); // Keys

                handler(wrapper -> {
                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    final byte[] encryptedNonce = wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);

                    wrapper.write(Type.BOOLEAN, chatSession1192 == null);
                    if (chatSession1192 != null) {
                        final long salt = ChatSession1_19_2.SECURE_RANDOM.nextLong();
                        final byte[] signedNonce = chatSession1192.getSigner().sign(updater -> {
                            updater.update(wrapper.user().get(NonceStorage.class).nonce());
                            updater.update(Longs.toByteArray(salt));
                        });

                        wrapper.write(Type.LONG, salt);
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signedNonce);
                    } else {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, encryptedNonce);
                    }
                });
            }
        }, true);

        registerServerbound(ServerboundPackets1_19_3.CHAT_COMMAND, ServerboundPackets1_19_1.CHAT_COMMAND, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                map(Type.VAR_INT); // Signatures
                handler(wrapper -> {
                    final int signatures = wrapper.get(Type.VAR_INT, 0);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    final CommandArgumentsProvider commandArgumentsProvider = Via.getManager().getProviders().get(CommandArgumentsProvider.class);

                    final boolean signingEnabled = chatSession1192 != null && commandArgumentsProvider != null;

                    for (int i = 0; i < signatures; i++) {
                        if (signingEnabled) {
                            wrapper.read(Type.STRING); // Argument name
                            wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE); // Signature
                        } else {
                            wrapper.passthrough(Type.STRING); // Argument name

                            // Signature
                            wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE);
                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]);
                        }
                    }

                    if (chatSession1192 != null) {
                        final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                        final String command = wrapper.get(Type.STRING, 0);
                        final long timestamp = wrapper.get(Type.LONG, 0);
                        final long salt = wrapper.get(Type.LONG, 1);

                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);

                        for (Pair<String, String> argument : commandArgumentsProvider.getSignedArguments(command)) {
                            final byte[] signature = chatSession1192.sign(sender, new MessageMetadataModel(argument.value(), timestamp, salt), messagesStorage.lastSignatures());

                            wrapper.write(Type.STRING, argument.key());
                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                        }
                    }

                    wrapper.write(Type.BOOLEAN, false); // No signed preview

                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    messagesStorage.resetUnacknowledgedCount();
                    wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                    wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                });

                read(Type.VAR_INT); // Offset
                read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
            }
        }, true);
        registerServerbound(ServerboundPackets1_19_3.CHAT_MESSAGE, ServerboundPackets1_19_1.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                read(OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE); // Signature
                handler(wrapper -> {
                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);

                    if (chatSession1192 != null) {
                        final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                        final String message = wrapper.get(Type.STRING, 0);
                        final long timestamp = wrapper.get(Type.LONG, 0);
                        final long salt = wrapper.get(Type.LONG, 1);

                        final byte[] signature = chatSession1192.sign(sender, new MessageMetadataModel(message, timestamp, salt), messagesStorage.lastSignatures());

                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                        wrapper.write(Type.BOOLEAN, false); // Signed Preview - not implemented yet, but I could do it
                    } else {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]); // Signature
                        wrapper.write(Type.BOOLEAN, false); // No signed preview
                    }

                    messagesStorage.resetUnacknowledgedCount();
                    wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                    wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                });

                read(Type.VAR_INT); // Offset
                read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
            }
        }, true);
    }
}
