// Pre-bundles Monaco's ESM distribution into static assets under `src/jsMain/resources/public/monaco`.
// Monaco's AMD build (`min/vs`) is deprecated since 0.53 and its custom workers no longer work, so the ESM
// build is the only supported path. Bundling it here rather than through Kobweb's webpack keeps Monaco out
// of the main bundle (it would otherwise cost ~6 MB on every page) while still producing plain static files,
// which is what `kobwebExport` - it copies a single script - can actually ship.
//
// Usage: node build-monaco.mjs <outDir>
import * as esbuild from 'esbuild';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const here = path.dirname(fileURLToPath(import.meta.url));
const outdir = process.argv[2] ?? path.resolve(here, '../src/jsMain/resources/public/monaco');

// The playground only ever shows Kotlin sources and generated JSON, so every other language is dead weight.
// JSON is the odd one out: it has no `languages/definitions/json`, its highlighting comes from the
// LSP-backed service under `languages/features/json` instead - which is why it is kept on the other axis
// and needs its own worker (see the `json.worker.js` build below).
const keptDefinitions = new Set(['kotlin']);
const keptFeatures = new Set(['json']);

// `editor.main.js` hardcodes an import of all 82 grammars under `languages/definitions/<id>/register.js`
// plus the four heavyweight language *services* under `languages/features/<id>/register.js` (css, html,
// json, typescript). Stubbing them out beats hand-copying `editor.main.js` minus a few lines, which would
// silently rot on every Monaco bump. The services matter most: each one transitively imports the editor
// stylesheet, so esbuild duplicates the full 345 kB CSS into every one of their chunks.
const stubUnusedLanguages = {
	name: 'stub-unused-languages',
	setup(build) {
		const register = /[\\/]languages[\\/](definitions|features)[\\/]([^\\/]+)[\\/]register\.js$/;

		build.onResolve({ filter: register }, args => {
			const [, kind, id] = register.exec(args.path);
			const kept = kind === 'definitions' ? keptDefinitions : keptFeatures;
			if (kept.has(id)) return null;
			return { path: args.path, namespace: 'monaco-stub' };
		});

		build.onLoad({ filter: /.*/, namespace: 'monaco-stub' }, () => ({ contents: '', loader: 'js' }));
	},
};

const shared = {
	bundle: true,
	legalComments: 'none',
	loader: { '.ttf': 'dataurl' },
	minify: true,
	plugins: [stubUnusedLanguages],
	target: 'es2020',
};

// The editor itself: ESM + splitting so each language grammar stays a lazily fetched chunk.
const editor = await esbuild.build({
	...shared,
	entryPoints: [path.resolve(here, 'monaco.entry.mjs')],
	entryNames: 'monaco',
	format: 'esm',
	metafile: true,
	outdir,
	splitting: true,
});

// The web workers: IIFE so they can be started as classic workers from a static URL.
for (const worker of ['editor.worker', 'json.worker']) {
	await esbuild.build({
		...shared,
		entryPoints: [path.resolve(here, `${worker}.entry.mjs`)],
		format: 'iife',
		outfile: path.join(outdir, `${worker}.js`),
	});
}

const outputs = Object.entries(editor.metafile.outputs);
const total = outputs.reduce((sum, [, o]) => sum + o.bytes, 0);
const entry = outputs.find(([file]) => file.endsWith('monaco.js'));
console.log(
	`Monaco bundled -> ${outdir} (${outputs.length} files, ${(total / 1024 / 1024).toFixed(1)} MB total, ` +
	`entry ${(entry[1].bytes / 1024).toFixed(0)} kB)`
);
