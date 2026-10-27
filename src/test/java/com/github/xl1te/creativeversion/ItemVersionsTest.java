package com.github.xl1te.creativeversion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemVersionsTest {
	@Test
	void comparesVersionsNumerically() {
		assertTrue(ItemVersions.compare("1.9", "1.21") < 0);
		assertTrue(ItemVersions.compare("1.20.5", "1.20.4") > 0);
		assertTrue(ItemVersions.compare("26.1", "26.3") < 0);
		assertTrue(ItemVersions.compare("1.21", "1.21.0") == 0);
	}
}
