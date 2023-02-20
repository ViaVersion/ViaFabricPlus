/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_19_1to1_19;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.Protocol1_19_1To1_19;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ServerboundPackets1_19;
import de.florianmichael.viafabricplus.definition.v1_19_0.MessageMetadataModel;
import de.florianmichael.viafabricplus.definition.v1_19_0.provider.CommandArgumentsProvider;
import de.florianmichael.viafabricplus.definition.v1_19_0.storage.ChatSession1_19_0;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Protocol1_19_1To1_19.class)
public class MixinProtocol1_19_1To1_19 extends AbstractProtocol<ClientboundPackets1_19, ClientboundPackets1_19_1, ServerboundPackets1_19, ServerboundPackets1_19_1> {

    @Inject(method = "registerPackets", at = @At("RETURN"), remap = false)
    public void injectRegisterPackets(CallbackInfo ci) {
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketHandlers() {
            public void register() {
                this.map(Type.STRING);
                this.map(Type.OPTIONAL_PROFILE_KEY);
                handler(wrapper -> {
                    final ChatSession1_19_0 chatSession1190 = wrapper.user().get(ChatSession1_19_0.class);
                    if (chatSession1190 != null) {
                        final ProfileKey profileKey = wrapper.get(Type.OPTIONAL_PROFILE_KEY, 0);
                        if (profileKey != null) {
                            wrapper.set(Type.OPTIONAL_PROFILE_KEY, 0, new ProfileKey(
                                    profileKey.expiresAt(),
                                    profileKey.publicKey(),
                                    chatSession1190.getLegacyKey()
                            ));
                        }
                    }
                });
                this.read(Type.OPTIONAL_UUID);
            }
        }, true);
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketHandlers() {
            public void register() {
            }
        }, true);
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketHandlers() {
            public void register() {
            }
        }, true);
        this.registerServerbound(ServerboundPackets1_19_1.CHAT_MESSAGE, ServerboundPackets1_19.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Message
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                map(Type.BYTE_ARRAY_PRIMITIVE); // Signature
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String message = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ChatSession1_19_0 chatSession1190 = wrapper.user().get(ChatSession1_19_0.class);
                    if (chatSession1190 != null) {
                        if (sender == null) {
                            throw new IllegalStateException("ViaVersion didn't track the connected UUID correctly, please check your BaseProtocol1_7");
                        }

                        wrapper.set(Type.BYTE_ARRAY_PRIMITIVE, 0, chatSession1190.sign(
                                sender,
                                new MessageMetadataModel(
                                        message,
                                        timestamp,
                                        salt
                                )
                        ));
                    }
                });
                map(Type.BOOLEAN); // Signed preview
                read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen messages
                read(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE); // Last received message
            }
        }, true);
        this.registerServerbound(ServerboundPackets1_19_1.CHAT_COMMAND, ServerboundPackets1_19.CHAT_COMMAND, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                map(Type.VAR_INT); // Signatures

                // Emulating old signatures
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String command = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ChatSession1_19_0 chatSession1190 = wrapper.user().get(ChatSession1_19_0.class);
                    if (chatSession1190 != null) {
                        if (sender == null) {
                            throw new IllegalStateException("ViaVersion didn't track the connected UUID correctly, please check your BaseProtocol1_7");
                        }

                        // Make sure we implemented the command signing
                        final CommandArgumentsProvider commandArgumentsProvider = Via.getManager().getProviders().get(CommandArgumentsProvider.class);
                        if (commandArgumentsProvider != null) {
                            // Removing new signatures
                            {
                                final int signatures = wrapper.get(Type.VAR_INT, 0);
                                for (int i = 0; i < signatures; i++) {
                                    wrapper.read(Type.STRING); // Argument name
                                    wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); // Signature
                                }
                            }

                            // Signing arguments
                            {
                                final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                                if (messagesStorage != null) {
                                    for (Pair<String, String> argument : commandArgumentsProvider.getSignedArguments(command)) {
                                        final byte[] signature = chatSession1190.sign(
                                                sender,
                                                new MessageMetadataModel(
                                                        argument.getRight(),
                                                        timestamp,
                                                        salt
                                                )
                                        );


                                        wrapper.write(Type.STRING, argument.getLeft());
                                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                                    }
                                }
                            }
                        }
                    }
                });
                map(Type.BOOLEAN); // Signed preview
                read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen messages
                read(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE); // Last received message
            }
        }, true);
    }
}
