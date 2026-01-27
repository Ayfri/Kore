---
root: .components.layouts.MarkdownLayout
title: Known Issues
nav-title: Known Issues
description: A list of known issues and limitations in Kore.
keywords: kore, guide, documentation, known issues
date-created: 2025-08-27
date-modified: 2025-08-27
routeOverride: /docs/known-issues
---
# Missing features

## SNBT

Some SNBT features are not supported yet. Kore depends on another library for writing SNBT which does not support them yet. <br>
The main features that are not supported are:
- Heterogeneous lists (e.g. `[1, "string", {key: "value"}]`)
- SNBT operations (`bool(arg)`, `uuid(arg)`)

Such features would be very hard to implement just in Kore, but if you really need them, maybe we could consider creating our own SNBT library.

## Resource Packs

Kore is only designed to create Data Packs, not Resource Packs, but it could be implemented in the future.

## Updates

Kore is sometimes a little bit outdated, and some features may not work as expected. I am working on my free time to keep it up to date, so fill free to help me update it by contributing to the project.
