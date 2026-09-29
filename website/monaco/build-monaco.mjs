// Pre-bundles Monaco's ESM distribution into static assets under `src/jsMain/resources/public/monaco`.
// Monaco's AMD build (`min/vs`) is deprecated since 0.53 and its custom workers no longer work, so the ESM
// build is the only supported path. Bundling it here rather than through Kobweb's webpack keeps Monaco out
// of the main bundle (it would otherwise cost ~6 MB on every page) while still producing plain static files,
// which is what `kobwebExport` - it copies a single script - can actually ship.
//
// Usage: node build-monaco.mjs <outDir>
import * as esbuild from 'esbuild';
import fs from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const here = path.dirname(fileURLToPath(import.meta.url));
const outdir = process.argv[2] ?? path.resolve(here, '../src/jsMain/resources/public/monaco');

// `editor.main.js` hardcodes imports of all 82 grammars under `languages/definitions/<id>/register.js`, the css,
// html, json and typescript services under `languages/features/<id>/register.js` and an LSP client. The playground's
// one editor is Kotlin (its JSON previews go through Prism), so everything else is stubbed out rather than hand-copying
// `editor.main.js` minus a few lines, which would silently rot on every Monaco bump.
const unused = /[\\/](languages[\\/](definitions|features)[\\/][^\\/]+[\\/]register|monaco-lsp-client[\\/]out[\\/]index)\.js$/;
const kept = /[\\/]definitions[\\/]kotlin[\\/]/;
const stubUnused = {
	name: 'stub-unused',
	setup(build) {
		build.onResolve({ filter: unused }, args => kept.test(args.path) ? null : { path: args.path, namespace: 'monaco-stub' });
		build.onLoad({ filter: /.*/, namespace: 'monaco-stub' }, () => ({ contents: '', loader: 'js' }));
	},
};

const shared = {
	bundle: true,
	legalComments: 'none',
	loader: { '.ttf': 'dataurl' },
	minify: true,
	plugins: [stubUnused],
	target: 'es2020',
};

// Chunk names are content hashes, so the previous build's chunks would otherwise pile up and ship.
fs.rmSync(outdir, { force: true, recursive: true });

// The editor itself: ESM + splitting so the Kotlin language configuration stays a lazily fetched chunk.
const editor = await esbuild.build({
	...shared,
	entryPoints: [path.resolve(here, 'monaco.entry.mjs')],
	entryNames: 'monaco',
	format: 'esm',
	metafile: true,
	outdir,
	splitting: true,
});

// The web worker: IIFE so it can be started as a classic worker from a static URL.
await esbuild.build({
	...shared,
	entryPoints: [path.resolve(here, 'editor.worker.entry.mjs')],
	format: 'iife',
	outfile: path.join(outdir, 'editor.worker.js'),
});

const outputs = Object.entries(editor.metafile.outputs);
const total = outputs.reduce((sum, [, o]) => sum + o.bytes, 0);
const entry = outputs.find(([file]) => file.endsWith('monaco.js'));
console.log(
	`Monaco bundled -> ${outdir} (${outputs.length} files, ${(total / 1024 / 1024).toFixed(1)} MB total, ` +
	`entry ${(entry[1].bytes / 1024).toFixed(0)} kB)`
);
