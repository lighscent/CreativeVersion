package com.github.xl1te.creativeversion;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class ViaChannels {
	public static final CustomPacketPayload.Type<ViaPayload> SERVER =
			new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("vv", "server_details"));
	public static final CustomPacketPayload.Type<ViaPayload> PROXY =
			new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("vv", "proxy_details"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ViaPayload> SERVER_CODEC = ViaPayload.codecFor(SERVER);
	public static final StreamCodec<RegistryFriendlyByteBuf, ViaPayload> PROXY_CODEC = ViaPayload.codecFor(PROXY);

	private ViaChannels() {
	}
}
