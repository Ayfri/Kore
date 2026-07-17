---
root: .components.layouts.MarkdownLayout
title: Multiplatform Support
nav-title: Multiplatform
description: Kore's kore, oop, helpers, and bindings modules are Kotlin Multiplatform (JVM + JS). Generate real datapacks on Node.js, or export strings/zip bytes anywhere.
keywords: kore, multiplatform, kotlin multiplatform, kotlin/js, jvm, kmp, nodejs, exportAsStrings, generateZipBytes, kotest, karma
date-created: 2026-07-12
date-modified: 2026-07-12
routeOverride: /docs/advanced/multiplatform
---

# Multiplatform Support

`kore`, `oop`, `helpers`, and `bindings` are all **Kotlin Multiplatform** libraries: each declares a `jvm()` target and a
`js { browser(); nodejs() }` target, built from a shared `commonMain` source set. There is no separate "Kore JS"
artifact - the same `io.github.ayfri.kore:<module>:VERSION` coordinates publish JVM, JS, and common metadata variants
side by side.

## What runs where

| Module     | JVM                                                                              | JS (Node.js)                                                                                           | JS (browser)                                                           |
|------------|----------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------|
| `kore`     | Full DSL + `generate()` / `generateZip()` / `generateJar()` (file, ZIP, JAR I/O) | Full DSL + real `generate()` / `generateZip()` / `generateJar()`, backed by Node's `fs`                | Full DSL, JSON/NBT building, `exportAsStrings()`, `generateZipBytes()` |
| `oop`      | Full gameplay abstractions                                                       | Full gameplay abstractions (built on `kore`'s common API)                                              | Full gameplay abstractions                                             |
| `helpers`  | Full helper utilities                                                            | Full helper utilities                                                                                  | Full helper utilities                                                  |
| `bindings` | Full: import, download, codegen to disk                                          | Exploration and code-generation model compile, but there is no supported JS-side downloader/writer yet | Same as Node.js                                                        |

The DSL itself - building a `dataPack { }`, commands, data-driven features, JSON/NBT - is written once in `commonMain`
and behaves identically on every target. **File system access** for `kore` works on both the JVM and Node.js (real
files, ZIPs, and JARs); the browser has no real filesystem, so it only gets the in-memory paths (`exportAsStrings()`,
`generateZipBytes()`). `bindings`' network downloaders and disk caching stay JVM-only, since there's no common
Kotlin abstraction for those across JVM/browser/Node yet.

## Using `kore` from Kotlin/JS

`kore` has no `@JsExport` declarations, so this is a **Kotlin-to-Kotlin** target: it's for a project written in Kotlin
that compiles to JS (browser or Node), not for importing from plain JavaScript/TypeScript via npm. If you're writing
Kotlin and targeting `js()`, `kore` is just another multiplatform dependency.

### In Node.js (Kotlin/JS with `nodejs()`)

Node has a real filesystem, and `kore` uses it directly - `generate()`, `generateZip()`, and `generateJar()` behave
exactly like they do on the JVM, no extra interop code needed:

```kotlin
val pack = dataPack("my_pack") {
	function("hello") { say("hi") }
}

pack.path("out") // kotlinx.io.files.Path or a String, same as the JVM guide
pack.generate()  // writes out/my_pack/data/... to Node's real fs
pack.generateZip() // writes out/my_pack.zip
```

This works because Kore's generator is built on `kotlinx.io.files.SystemFileSystem`, which already targets Node's
`fs` module under the hood - there's no `external`/`@JsModule("fs")` declaration to write yourself. ZIP/JAR bytes
are produced by Kore's own pure-Kotlin ZIP writer (no compression library dependency), and merging with an existing
third-party `.zip` datapack (`mergeWithPacks`) works too, decompressing STORE and DEFLATE entries with a pure-Kotlin
inflate implementation.

This is useful for a Kore-based build step in a Kotlin/JS or Kotlin Multiplatform project that doesn't run a JVM at
all - for example, a Node-based CI step or CLI tool written entirely in Kotlin/JS.

### In a browser (Kotlin/JS with `browser()`)

The browser has no real filesystem, so `generate()` / `generateZip()` / `generateJar()` aren't available there - use
the in-memory exports instead:

```kotlin
val pack = dataPack("my_pack") {
	function("hello") { say("hi") }
}

val files: Map<String, String> = pack.exportAsStrings()
// files["data/my_pack/function/hello.mcfunction"] == "say hi"

val zipBytes: ByteArray = pack.generateZipBytes() // suspend, works on every target
```

`generateZipBytes()` is the one archive-producing call that works everywhere, including the browser: it's a `suspend`
function that stages files through the [Origin Private File System](https://developer.mozilla.org/en-US/docs/Web/API/File_System_API/Origin_private_file_system)
(OPFS) behind the scenes and returns the finished ZIP as a plain `ByteArray`, ready to hand to a `Blob` +
`URL.createObjectURL` download link (both reachable via Kotlin/JS's `kotlinx.browser` / `org.w3c.dom` bindings, no
extra dependency). This is the shape you'd use for something like an in-browser Kore playground: build the pack with
the same DSL as the JVM guide, call `generateZipBytes()` (or `exportAsStrings()` if you'd rather hand off individual
files instead of an archive), and wire the result into whichever browser API triggers the download.

## `bindings` on JS

`bindings`' exploration, entity model, and code generation (`KtRenderer`/`KtType`, a hand-rolled common codegen model
that replaced KotlinPoet) live in `commonMain` and compile for the JS target. The `importDatapacks { }` DSL entry
point, GitHub/CurseForge/Modrinth/local downloaders, and disk caching stay `jvmMain`-only, since they depend on
network I/O. In practice, use `bindings` from the JVM as documented in [Bindings](/docs/advanced/bindings).

## Testing across platforms

`kore`, `oop`, `helpers`, and `bindings` all run their [Kotest](https://kotest.io) suites on every Kotlin
Multiplatform target: the JVM, Node.js (`jsNodeTest`), and a headless-Chrome browser via Karma (`jsBrowserTest`).
Most test specs live in `commonTest` and build a `dataPack { }` in memory, so the same `FunSpec` executes identically
on all three platforms. A `dataPack` that never calls `generate()` / `generateZip()` / `generateJar()` is
browser-safe by construction, since those calls are the only part of the pipeline that touches a real filesystem.

Each module's built-in `allTests` task aggregates its JVM/Node/browser test tasks, and the root `testAll` task
aggregates `allTests` across all four modules - this is what CI runs.

## Adding the dependency for a JS target

Same coordinates as JVM, resolved through Gradle's Kotlin Multiplatform metadata:

```kotlin
kotlin {
	jvm()
	js { browser(); nodejs() }

	sourceSets {
		commonMain.dependencies {
			implementation("io.github.ayfri.kore:kore:VERSION")
		}
	}
}
```

## See also

- [Getting Started](/docs/getting-started) - the JVM-first walkthrough (`generate()` / `generateZip()`), works the
  same way on Node.js.
- [Creating A Datapack](/docs/guides/creating-a-datapack) - output paths and generation options.
- [Bindings](/docs/advanced/bindings) - the datapack importer, JVM usage.
- [Known Issues](/docs/advanced/known-issues) - other documented limitations.
