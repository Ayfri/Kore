// Entry point for the pre-bundled Monaco editor, built by `bundleMonaco` (see build.gradle.kts).
// `editor.main.js` registers every built-in language; their grammars stay behind dynamic imports, so
// esbuild's code splitting keeps them out of the initial chunk.
import { languages } from 'monaco-editor/editor/editor.main.js';
import { kotlinLanguage } from './kotlin-grammar.mjs';

export * from 'monaco-editor/editor/editor.main.js';

// A tokenizer set directly wins over the lazy one `editor.main.js` registered, which then never loads.
languages.setMonarchTokensProvider('kotlin', kotlinLanguage);
