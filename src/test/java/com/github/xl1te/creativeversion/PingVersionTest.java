package com.github.xl1te.creativeversion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PingVersionTest {
	@Test
	void takesASingleVersion() {
		assertEquals("1.20.1", PingVersion.detect("1.20.1"));
		assertEquals("26.3", PingVersion.detect("Paper 26.3"));
	}

	@Test
	void rejectsRanges() {
		assertNull(PingVersion.detect("1.8.x-1.21.x"));
		assertNull(PingVersion.detect("Velocity 1.7.2-26.3"));
		assertNull(PingVersion.detect("www.monserveur.fr"));
	}
}
