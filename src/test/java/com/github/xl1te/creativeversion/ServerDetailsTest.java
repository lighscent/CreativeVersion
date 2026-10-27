package com.github.xl1te.creativeversion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServerDetailsTest {
	@BeforeAll
	static void load() {
		ItemVersions.load();
	}

	@Test
	void resolvesProtocolNumberFirst() {
		assertEquals("1.20", ServerDetails.resolve("{\"specVersion\":1,\"version\":763,\"versionName\":\"1.20.1\"}"));
		assertEquals("1.20.2", ServerDetails.resolve("{\"specVersion\":1,\"version\":764,\"versionName\":\"weird\"}"));
	}

	@Test
	void fallsBackToVersionName() {
		assertEquals("1.20.1", ServerDetails.resolve("{\"specVersion\":1,\"version\":999999,\"versionName\":\"Paper 1.20.1\"}"));
	}

	@Test
	void rejectsGarbage() {
		assertNull(ServerDetails.resolve("not json"));
		assertNull(ServerDetails.resolve("{\"specVersion\":1}"));
		assertNull(ServerDetails.resolve("{\"specVersion\":1,\"version\":999999,\"versionName\":\"???\"}"));
	}
}
