// Entry point for the pre-bundled Monaco editor, built by `bundleMonaco` (see build.gradle.kts).
// `editor.main.js` registers Kotlin, the one language `build-monaco.mjs` keeps; its configuration stays behind a
// dynamic import, so esbuild's code splitting keeps it out of the initial chunk.
import { languages } from 'monaco-editor/editor/editor.main.js';
import { kotlinLanguage } from './kotlin-grammar.mjs';

export * from 'monaco-editor/editor/editor.main.js';

// A tokenizer set directly wins over the lazy one `editor.main.js` registered, which then never loads.
languages.setMonarchTokensProvider('kotlin', kotlinLanguage);
