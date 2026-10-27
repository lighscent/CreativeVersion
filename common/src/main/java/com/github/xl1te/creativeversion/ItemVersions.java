package com.github.xl1te.creativeversion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class ItemVersions {
	private static Map<String, String> since = Map.of();
	private static Map<Integer, String> protocols = Map.of();

	static void load() {
		try (var in = ItemVersions.class.getResourceAsStream("/assets/creativeversion/item_versions.json")) {
			if (in == null) {
				throw new IllegalStateException("item_versions.json missing from the jar");
			}
			JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
			Map<String, String> map = new HashMap<>();
			for (var entry : root.getAsJsonObject("since").entrySet()) {
				map.put(entry.getKey(), entry.getValue().getAsString());
			}
			since = map;
			Map<Integer, String> table = new HashMap<>();
			if (root.has("protocols") && root.get("protocols").isJsonObject()) {
				for (var entry : root.getAsJsonObject("protocols").entrySet()) {
					table.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
				}
			}
			protocols = table;
			Log.LOGGER.info("Loaded {} item versions", map.size());
		} catch (Exception e) {
			Log.LOGGER.error("Could not load item versions, mod inactive", e);
		}
	}

	static String versionForProtocol(int protocol) {
		return protocols.get(protocol);
	}

	static boolean addedAfter(String id, String backend) {
		String added = since.get(id);
		return added != null && compare(added, backend) > 0;
	}

	static int compare(String a, String b) {
		String[] left = a.split("\\.");
		String[] right = b.split("\\.");
		for (int i = 0; i < Math.max(left.length, right.length); i++) {
			int l = i < left.length ? Integer.parseInt(left[i]) : 0;
			int r = i < right.length ? Integer.parseInt(right[i]) : 0;
			if (l != r) {
				return Integer.compare(l, r);
			}
		}
		return 0;
	}
}

