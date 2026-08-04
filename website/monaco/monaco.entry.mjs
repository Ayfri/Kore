// Entry point for the pre-bundled Monaco editor, built by `bundleMonaco` (see build.gradle.kts).
// `editor.main.js` registers every built-in language; their grammars stay behind dynamic imports, so
// esbuild's code splitting keeps them out of the initial chunk.
export * from 'monaco-editor/editor/editor.main.js';
