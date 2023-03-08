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
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.BitSetType;
import com.viaversion.viaversion.api.type.types.ByteArrayType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viafabricplus.definition.v1_19_0.MessageMetadataModel;
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
    private static final ByteArrayType.OptionalByteArrayType OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType.OptionalByteArrayType(256);

    @Unique
    private static final ByteArrayType MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType(256);

    @Unique
    private static final BitSetType ACKNOWLEDGED_BIT_SET_TYPE = new BitSetType(20);

    @Unique
    private static final UUID ZERO_UUID = new UUID(0, 0);

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
                map(Type.STRING);
                handler(wrapper -> {
                    final UUID uuid = wrapper.read(Type.OPTIONAL_UUID);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    wrapper.write(Type.OPTIONAL_PROFILE_KEY, chatSession1192 != null ? chatSession1192.getProfileKey() : null);

                    wrapper.write(Type.OPTIONAL_UUID, uuid);
                });
            }
        }, true);
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE_ARRAY_PRIMITIVE); // Keys
                create(Type.BOOLEAN, true); // Is nonce

                // Removing new nonce if chat session is connected
                handler(wrapper -> {
                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); // Encrypted Nonce
                    }
                });

                // Writing original packet data if chat session is connected
                handler(wrapper -> {
                    final NonceStorage nonceStorage = wrapper.user().get(NonceStorage.class);
                    if (nonceStorage != null) {
                        final byte[] nonce = nonceStorage.nonce();
                        if (nonce == null) {
                            throw new IllegalStateException("Didn't tracked the packet nonce???");
                        }
                        final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                        if (chatSession1192 != null) {
                            wrapper.set(Type.BOOLEAN, 0, false); // Now it's a nonce
                            final long salt = ChatSession1_19_2.SECURE_RANDOM.nextLong();
                            final byte[] signedNonce = chatSession1192.getSigner().sign(updater -> {
                                if (updater != null) {
                                    updater.update(nonce);
                                    updater.update(Longs.toByteArray(salt));
                                }
                            });

                            wrapper.write(Type.LONG, salt);
                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signedNonce);
                        }
                    }
                });
            }
        }, true);

        this.registerClientbound(ClientboundPackets1_19_1.PLAYER_CHAT, ClientboundPackets1_19_3.DISGUISED_CHAT, new PacketHandlers() {
            @Override
            public void register() {
                read(Type.OPTIONAL_BYTE_ARRAY_PRIMITIVE); // Previous signature
                handler(wrapper -> {
                    final PlayerMessageSignature signature = wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE);

                    // Store message signature for last seen
                    if (!signature.uuid().equals(ZERO_UUID) && signature.signatureBytes().length != 0) {
                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                        if (messagesStorage != null) {
                            messagesStorage.add(signature);
                            if (messagesStorage.tickUnacknowledged() > 64) {
                                messagesStorage.resetUnacknowledgedCount();

                                // Send chat acknowledgement
                                final PacketWrapper chatAckPacket = wrapper.create(ServerboundPackets1_19_1.CHAT_ACK);
                                chatAckPacket.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                                wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);

                                chatAckPacket.sendToServer(Protocol1_19_3To1_19_1.class);
                            }
                        }
                    }

                    final String plainMessage = wrapper.read(Type.STRING);
                    JsonElement decoratedMessage = wrapper.read(Type.OPTIONAL_COMPONENT);

                    wrapper.read(Type.LONG); // Timestamp
                    wrapper.read(Type.LONG); // Salt
                    wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen

                    final JsonElement unsignedMessage = wrapper.read(Type.OPTIONAL_COMPONENT);
                    if (unsignedMessage != null) {
                        decoratedMessage = unsignedMessage;
                    }
                    if (decoratedMessage == null) {
                        decoratedMessage = GsonComponentSerializer.gson().serializeToTree(Component.text(plainMessage));
                    }

                    final int filterMaskType = wrapper.read(Type.VAR_INT);
                    if (filterMaskType == 2) { // Partially filtered
                        wrapper.read(Type.LONG_ARRAY_PRIMITIVE); // Mask
                    }

                    wrapper.write(Type.COMPONENT, decoratedMessage);
                    // Keep chat type at the end
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

                // Signature removing if we have a chat session
                handler(wrapper -> {
                    final int signatures = wrapper.get(Type.VAR_INT, 0);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        final CommandArgumentsProvider commandArgumentsProvider = Via.getManager().getProviders().get(CommandArgumentsProvider.class);
                        if (commandArgumentsProvider != null) {
                            for (int i = 0; i < signatures; i++) {
                                wrapper.read(Type.STRING); // Argument name
                                wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE); // Signature
                            }
                            return;
                        }
                    }

                    for (int i = 0; i < signatures; i++) {
                        wrapper.passthrough(Type.STRING); // Argument name

                        // Signature
                        wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE);
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]);
                    }
                });

                // Removing new acknowledgement
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // Offset
                    wrapper.read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
                });

                // Signing all arguments
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String command = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        if (sender == null) {
                            throw new IllegalStateException("ViaVersion didn't track the connected UUID correctly, please check your BaseProtocol1_7");
                        }
                        final CommandArgumentsProvider commandArgumentsProvider = Via.getManager().getProviders().get(CommandArgumentsProvider.class);
                        if (commandArgumentsProvider != null) {
                            // Signing arguments
                            {
                                final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                                if (messagesStorage != null) {
                                    for (Pair<String, String> argument : commandArgumentsProvider.getSignedArguments(command)) {
                                        final byte[] signature = chatSession1192.sign(
                                                sender,
                                                new MessageMetadataModel(
                                                        argument.value(),
                                                        timestamp,
                                                        salt
                                                ),
                                                messagesStorage.lastSignatures()
                                        );


                                        wrapper.write(Type.STRING, argument.key());
                                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                                    }
                                }
                            }
                        }
                    }
                    wrapper.write(Type.BOOLEAN, false); // No signed preview
                });

                // Adding old acknowledgement
                handler(wrapper -> {
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    if (messagesStorage != null) {
                        messagesStorage.resetUnacknowledgedCount();
                        wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                        wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                    }
                });
            }
        }, true);
        registerServerbound(ServerboundPackets1_19_3.CHAT_MESSAGE, ServerboundPackets1_19_1.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                handler(wrapper -> {
                    wrapper.read(OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE); // Signature

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 == null) {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]); // Signature
                        wrapper.write(Type.BOOLEAN, false); // No signed preview
                    }
                });

                // Emulate old Message chain
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String message = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        if (sender == null) {
                            throw new IllegalStateException("ViaVersion didn't track the connected UUID correctly, please check your BaseProtocol1_7");
                        }
                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                        if (messagesStorage != null) {
                            final byte[] signature = chatSession1192.sign(
                                    sender,
                                    new MessageMetadataModel(
                                            message,
                                            timestamp,
                                            salt
                                    ),
                                    messagesStorage.lastSignatures()
                            );

                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                            wrapper.write(Type.BOOLEAN, false); // Signed Preview - not implemented yet, but i could do it
                        }
                    }
                });

                // Removing new acknowledgement
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // Offset
                    wrapper.read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
                });

                // Adding old acknowledgement
                handler(wrapper -> {
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    if (messagesStorage != null) {
                        messagesStorage.resetUnacknowledgedCount();
                        wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                        wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                    }
                });
            }
        }, true);
    }
}
