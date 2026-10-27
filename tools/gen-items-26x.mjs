import { execFileSync } from "node:child_process";
import { readFileSync, writeFileSync } from "node:fs";
import { homedir } from "node:os";
import { join } from "node:path";

const JAR = join(homedir(), ".gradle", "caches", "fabric-loom", "minecraftMaven",
	"net", "minecraft", "minecraft-merged-deobf");
const JAVA = "C:\\Program Files\\Zulu\\zulu-25\\bin\\jar.exe";
const OUT = new URL("../common/src/main/resources/assets/creativeversion/item_versions.json", import.meta.url);

function itemIds(mcVersion) {
	const jar = join(JAR, mcVersion, `minecraft-merged-deobf-${mcVersion}.jar`);
	const list = execFileSync(JAVA, ["tf", jar], { encoding: "utf8", maxBuffer: 256 * 1024 * 1024 });
	return new Set(list.split(/\r?\n/)
		.filter(l => l.startsWith("assets/minecraft/items/") && l.endsWith(".json"))
		.map(l => l.slice("assets/minecraft/items/".length, -".json".length)));
}

const data = JSON.parse(readFileSync(OUT, "utf8"));
const chain = ["26.1.2", "26.2", "26.3"];
let known = new Set(Object.keys(data.since));
for (const label of chain) {
	let ids;
	try {
		ids = itemIds(label);
	} catch {
		console.warn(`skip ${label}: jar not in cache`);
		continue;
	}
	let added = 0;
	for (const id of ids) {
		if (!known.has(id)) {
			data.since[id] = label;
			known.add(id);
			added++;
		}
	}
	console.log(`${label}: ${ids.size} models, ${added} new`);
}

writeFileSync(OUT, JSON.stringify(data));
console.log(`total ${Object.keys(data.since).length} items`);
