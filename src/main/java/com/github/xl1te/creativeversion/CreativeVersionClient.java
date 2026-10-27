package com.github.xl1te.creativeversion;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.HashSet;
import java.util.Set;

public class CreativeVersionClient implements ClientModInitializer {
	static String backend;
	static String backendOrigin;
	static Set<Item> unavailable = Set.of();

	@Override
	public void onInitializeClient() {
		ItemVersions.load();
		PayloadTypeRegistry.clientboundPlay().register(ViaChannels.SERVER, ViaChannels.SERVER_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ViaChannels.PROXY, ViaChannels.PROXY_CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ViaChannels.SERVER,
				(payload, context) -> context.client().execute(() -> onDetails(payload.json(), true, context.client())));
		ClientPlayNetworking.registerGlobalReceiver(ViaChannels.PROXY,
				(payload, context) -> context.client().execute(() -> onDetails(payload.json(), false, context.client())));
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onJoin(handler.getServerData() != null ? handler.getServerData() : client.getCurrentServer(), client));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			backend = null;
			backendOrigin = null;
			unavailable = Set.of();
		});
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			if (isUnavailable(stack)) {
				lines.add(Component.translatable("creativeversion.tooltip.absent", backend).withStyle(ChatFormatting.RED));
			}
		});
	}

	private static void onJoin(ServerData server, Minecraft client) {
		backend = null;
		backendOrigin = null;
		unavailable = Set.of();
		if (server == null) {
			return;
		}

		String version = detect(server);
		if (version == null) {
			String ping = server.version == null ? "?" : server.version.getString();
			Log.LOGGER.info("Backend version unknown for {} (ping version: {}), nothing marked", server.ip, ping);
			return;
		}

		setBackend(version, "ping", client);
	}

	static void onDetails(String json, boolean fromServer, Minecraft client) {
		if (client.getCurrentServer() == null && client.getSingleplayerServer() == null) {
			return;
		}
		if (!fromServer && "server".equals(backendOrigin)) {
			return;
		}
		String version = ServerDetails.resolve(json);
		if (version == null) {
			return;
		}
		setBackend(version, fromServer ? "server" : "proxy", client);
	}

	private static void setBackend(String version, String origin, Minecraft client) {
		backend = version;
		backendOrigin = origin;
		Set<Item> marked = new HashSet<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (ItemVersions.addedAfter(BuiltInRegistries.ITEM.getKey(item).getPath(), version)) {
				marked.add(item);
			}
		}
		unavailable = marked;
		String source = switch (origin) {
			case "server", "proxy" -> "creativeversion.source.via";
			default -> "creativeversion.source.detected";
		};
		Log.LOGGER.info("Backend {} ({}), {} items marked", version, origin, marked.size());
		Component prefix = Component.literal("[CreativeVersion] ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
		Component body = Component.translatable("creativeversion.announce",
				Component.literal(version).withStyle(ChatFormatting.GREEN),
				Component.translatable(source).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC),
				Component.literal(String.valueOf(marked.size())).withStyle(ChatFormatting.RED))
				.withStyle(ChatFormatting.GRAY);
		announce(client, Component.empty().append(prefix).append(body));
	}

	private static String detect(ServerData server) {
		return server.version == null ? null : PingVersion.detect(server.version.getString());
	}

	private static void announce(Minecraft client, Component message) {
		if (client.player != null) {
			client.player.sendSystemMessage(message);
		}
	}

	public static boolean isUnavailable(ItemStack stack) {
		return !stack.isEmpty() && unavailable.contains(stack.getItem());
	}
}


