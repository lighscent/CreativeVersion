package com.github.xl1te.creativeversion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ServerDetails {
	private ServerDetails() {
	}

	public static String resolve(String json) {
		try {
			JsonObject payload = JsonParser.parseString(json).getAsJsonObject();
			if (payload.has("version") && payload.get("version").isJsonPrimitive()
					&& payload.getAsJsonPrimitive("version").isNumber()) {
				String version = ItemVersions.versionForProtocol(payload.get("version").getAsInt());
				if (version != null) {
					return version;
				}
			}
			if (payload.has("versionName") && payload.get("versionName").isJsonPrimitive()) {
				return PingVersion.detect(payload.get("versionName").getAsString());
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
