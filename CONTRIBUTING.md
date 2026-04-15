# Contribution guidelines

## Start here

Read the contributor docs before opening a PR:

- [Contributing][contributing-docs]
- [Architecture and Patterns][architecture-docs]
- [Workflow][workflow-docs]

If you are adding a new data-driven resource in `kore`, also read [Creating a New Generator][new-generator-docs].

## Basic workflow

1. Start from a scoped issue or proposal.
2. Create a branch focused on one behavior change.
3. Make the smallest relevant module change.
4. Add or update tests in the touched module.
5. Update documentation when behavior or API changes.
6. Open a pull request that links the issue, tests, and docs.

Detailed issue and pull-request expectations live in [Workflow][workflow-docs].

For CI and release details, read:

- [CI/CD and Releases][releases-docs]

## Commit and PR title conventions

We follow conventional commits style for commit messages and PR titles.

Examples:

- `chore(minecraft): Increase Minecraft version to X.Y.Z.`
- `chore(project): Increase project version to X.Y.Z.`
- `feat(achievements): Add new \`test\` achievement trigger`.

## See also

- [README][readme]
- [Workflow][workflow-docs]

[architecture-docs]: https://kore.ayfri.com/docs/contributing/architecture-and-patterns

[contributing-docs]: https://kore.ayfri.com/docs/contributing/contributing

[new-generator-docs]: https://kore.ayfri.com/docs/contributing/creating-a-new-generator

[readme]: https://github.com/ayfri/kore/blob/master/README.md

[releases-docs]: https://kore.ayfri.com/docs/contributing/ci-cd-and-releases

[workflow-docs]: https://kore.ayfri.com/docs/contributing/contributing-workflow
