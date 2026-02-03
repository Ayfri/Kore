---
root: .components.layouts.MarkdownLayout
title: GitHub Actions Publishing
nav-title: GitHub Actions Publishing
description: Automatically publish your Kore datapacks using GitHub Actions with mc-publish.
keywords: minecraft, datapack, kore, github actions, publishing, automation, ci/cd
date-created: 2025-09-30
date-modified: 2025-09-30
routeOverride: /docs/advanced/github-actions-publishing
---

# GitHub Actions Publishing

This guide shows you how to automatically publish your Kore-generated datapacks to various platforms using GitHub Actions and the
`mc-publish` action.

## Prerequisites

- A GitHub repository containing your Kore project
- Accounts on target platforms (Modrinth, CurseForge, etc.)
- API tokens for each platform you want to publish to

## What is mc-publish?

[mc-publish](https://github.com/Kira-NT/mc-publish) is a GitHub Action that simplifies publishing Minecraft projects across multiple platforms including Modrinth, CurseForge, and GitHub Releases. It automatically detects project metadata and handles the complex publication process with minimal configuration.

## Setting Up GitHub Secrets

Before configuring your workflow, you'll need to add your platform tokens as GitHub secrets:

1. Go to your repository's **Settings** → **Secrets and variables** → **Actions**
2. Add the following secrets:
	- `MODRINTH_TOKEN`: Your Modrinth API token
	- `CURSEFORGE_TOKEN`: Your CurseForge API token
	- `GITHUB_TOKEN` is automatically provided by GitHub

> **Note:** Only add secrets for platforms you plan to publish to.

## Basic Workflow Setup

Create `.github/workflows/publish.yml` in your repository:

```yaml
name: Publish Datapack

on:
  release:
    types: [ published ]
  workflow_dispatch: # Allow manual triggering

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v5

      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          cache: gradle
          distribution: 'temurin'
          java-version: 21

      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Build datapack
        run: ./gradlew build

      - name: Publish to platforms
        uses: Kir-Antipov/mc-publish@v3.3
        with:
          # Modrinth configuration
          modrinth-id: YOUR_PROJECT_ID
          modrinth-token: ${{ secrets.MODRINTH_TOKEN }}

          # CurseForge configuration
          curseforge-id: YOUR_PROJECT_ID
          curseforge-token: ${{ secrets.CURSEFORGE_TOKEN }}

          # GitHub Releases
          github-token: ${{ secrets.GITHUB_TOKEN }}
```

Ensure to use `datapack.generateZip()` in your build script to generate a zip file containing your datapack.

## Advanced Configuration

For more control over the publishing process, you can customize various aspects:

```yaml
- name: Publish to platforms
  uses: Kir-Antipov/mc-publish@v3.3
  with:
    # Project identification
    modrinth-id: AANobbMI
    modrinth-token: ${{ secrets.MODRINTH_TOKEN }}
    curseforge-id: 394468
    curseforge-token: ${{ secrets.CURSEFORGE_TOKEN }}
    github-token: ${{ secrets.GITHUB_TOKEN }}

    # Version configuration
    name: "My Datapack v${{ github.ref_name }}"
    version: ${{ github.ref_name }}
    version-type: release # alpha, beta, or release

    # File selection
    files: |
      out/*.zip
      !out/*-unfinished.zip

    # Supported game versions
    game-versions: |
      1.21
      1.21.5
      1.21.6

    # Mod loaders (if exporting to Jars)
    loaders: |
      fabric
      forge

    # Release configuration
    changelog-file: CHANGELOG.md
    dependencies: |
      fabric-api(required){modrinth:P7dR8mSH}{curseforge:306612}

    # Platform-specific settings
    modrinth-featured: true
    curseforge-java-versions: |
      Java 21
    github-prerelease: ${{ contains(github.ref_name, 'beta') || contains(github.ref_name, 'alpha') }}
    github-draft: false
```

## Kore-Specific Configuration

## Workflow Triggers

You can configure when the publishing workflow runs:

```yaml
on:
  # Trigger on new releases
  release:
    types: [ published ]

  # Trigger on version tags
  push:
    tags:
      - 'v*'

  # Allow manual triggering
  workflow_dispatch:
    inputs:
      version-type:
        description: 'Release type'
        required: true
        default: 'release'
        type: choice
        options:
          - release
          - beta
          - alpha
```

## Platform-Specific Features

### Modrinth

- Supports featured projects
- Automatic dependency resolution
- Rich markdown changelog support

```yaml
modrinth-featured: true
modrinth-unfeature-mode: subset
```

### CurseForge

- Java version specification
- Custom display name support

```yaml
curseforge-java-versions: |
  Java 17
  Java 21
curseforge-display-name: "My Awesome Datapack"
```

### GitHub Releases

- Draft and prerelease support
- Asset management

```yaml
github-draft: false
github-prerelease: ${{ contains(github.ref_name, 'pre') }}
github-tag: ${{ github.ref_name }}
```

This helps mc-publish automatically detect project metadata.

## Best Practices

### 1. Version Management

Use semantic versioning and Git tags:

```bash
git tag v1.0.0
git push origin v1.0.0
```

### 2. Changelog Management

Maintain a `CHANGELOG.md` file following [Keep a Changelog](https://keepachangelog.com/) format:

```markdown
# Changelog

## [1.0.0] - 2025-09-30

### Added

- Initial release with basic functionality
- Support for Minecraft 1.21

### Changed

- Improved performance of item generation

### Fixed

- Fixed issue with advancement rewards
```

### 3. Testing Before Publishing

Add a test job before publishing:

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v5
      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run tests
        run: ./gradlew test

  publish:
    needs: test
    runs-on: ubuntu-latest
    # ... publishing steps
```

## Troubleshooting

### Common Issues

1. **Missing files**: Ensure you exported the datapack to the correct directory
2. **Invalid tokens**: Verify your secrets are correctly set in GitHub
3. **Permission errors**: Ensure the workflow has `contents: write` permission

### Debugging

Enable debug logging by adding:

```yaml
env:
  ACTIONS_RUNNER_DEBUG: true
  ACTIONS_STEP_DEBUG: true
```

## Complete Example

Here's a complete workflow for a Kore datapack project:

```yaml
name: Publish Datapack

on:
  release:
    types: [ published ]
  workflow_dispatch:

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v5

      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          cache: gradle
          distribution: 'temurin'
          java-version: 21

      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Generate datapack
        run: ./gradlew run

      - name: Publish to platforms
        uses: Kir-Antipov/mc-publish@v3.3
        with:
          name: "${{ github.event.repository.name }} ${{ github.ref_name }}"
          version: ${{ github.ref_name }}
          version-type: release

          files: ${{ github.event.repository.name }}-${{ github.ref_name }}.zip

          game-versions: |
            1.21
            1.21.5
            1.12.6

          modrinth-id: YOUR_MODRINTH_ID
          modrinth-token: ${{ secrets.MODRINTH_TOKEN }}
          modrinth-featured: true

          curseforge-id: YOUR_CURSEFORGE_ID
          curseforge-token: ${{ secrets.CURSEFORGE_TOKEN }}

          github-token: ${{ secrets.GITHUB_TOKEN }}
          github-tag: ${{ github.ref_name }}

          changelog-file: CHANGELOG.md
```

## See Also

- [Creating a Datapack](./creating-a-datapack)
- [Configuration](./configuration)
- [mc-publish Documentation](https://github.com/Kir-Antipov/mc-publish)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
