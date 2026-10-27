import { writeFileSync } from "node:fs";

const API = "https://api.github.com/repos/PrismarineJS/minecraft-data/contents/data/pc";
const RAW = "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pc";

const entries = await (await fetch(API)).json();
const versions = entries
	.map(e => e.name)
	.filter(n => /^\d+\.\d+(\.\d+)?$/.test(n))
	.sort((a, b) => {
		const pa = a.split(".").map(Number), pb = b.split(".").map(Number);
		return pa[0] - pb[0] || pa[1] - pb[1] || (pa[2] ?? 0) - (pb[2] ?? 0);
	});

const since = {};
const protocols = {};
for (const v of versions) {
	const versionRes = await fetch(`${RAW}/${v}/version.json`);
	if (versionRes.ok) {
		const protocol = (await versionRes.json()).version;
		if (!(protocol in protocols)) {
			protocols[protocol] = v;
		}
	} else {
		console.warn(`skip protocol ${v}: ${versionRes.status}`);
	}
	const res = await fetch(`${RAW}/${v}/items.json`);
	if (!res.ok) {
		console.warn(`skip ${v}: ${res.status}`);
		continue;
	}
	const items = await res.json();
	let added = 0;
	for (const item of items) {
		const id = item.name.replace(/^minecraft:/, "");
		if (!(id in since)) {
			since[id] = v;
			added++;
		}
	}
	console.log(`${v}: ${items.length} items, ${added} new`);
}

const out = { versions, since, protocols };
writeFileSync(new URL("../common/src/main/resources/assets/creativeversion/item_versions.json", import.meta.url), JSON.stringify(out));
console.log(`total ${Object.keys(since).length} items, ${versions.length} versions, ${Object.keys(protocols).length} protocols`);
