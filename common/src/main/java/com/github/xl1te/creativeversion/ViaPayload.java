package com.github.xl1te.creativeversion;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.nio.charset.StandardCharsets;

public final class ViaPayload implements CustomPacketPayload {
	private final CustomPacketPayload.Type<ViaPayload> type;
	private final String json;

	public ViaPayload(CustomPacketPayload.Type<ViaPayload> type, String json) {
		this.type = type;
		this.json = json;
	}

	public String json() {
		return json;
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return type;
	}

	public static StreamCodec<RegistryFriendlyByteBuf, ViaPayload> codecFor(CustomPacketPayload.Type<ViaPayload> type) {
		return new StreamCodec<>() {
			@Override
			public ViaPayload decode(RegistryFriendlyByteBuf buf) {
				byte[] bytes = new byte[buf.readableBytes()];
				buf.readBytes(bytes);
				return new ViaPayload(type, new String(bytes, StandardCharsets.UTF_8));
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buf, ViaPayload value) {
			}
		};
	}
}
