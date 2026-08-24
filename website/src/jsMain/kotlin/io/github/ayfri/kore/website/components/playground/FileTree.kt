package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.silk.components.icons.lucide.LucideBraces
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronDown
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFile
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileCog
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileText
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFolder
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFolderOpen
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTerminal
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** A datapack path split into what the tree draws: nested folders, and the files inside them. */
sealed interface FileNode {
	val name: String
	val path: String

	data class Folder(override val name: String, override val path: String, val children: List<FileNode>) : FileNode
	data class Leaf(override val name: String, override val path: String, val file: GeneratedFile) : FileNode
}

/**
 * Turns flat `exportAsStrings()` paths into a tree, folders first.
 *
 * Chains of single-child folders are merged into one row (`data/pack/function`), which is what keeps a
 * datapack's deep, mostly-empty directory layout readable in a narrow pane.
 */
fun buildFileTree(files: List<GeneratedFile>): List<FileNode> = buildNodes(files.map { it.path.split('/') to it }, "")

private fun buildNodes(entries: List<Pair<List<String>, GeneratedFile>>, prefix: String): List<FileNode> {
	val (leaves, nested) = entries.partition { (segments, _) -> segments.size == 1 }

	val folders = nested.groupBy { (segments, _) -> segments.first() }.map { (name, group) ->
		val path = if (prefix.isEmpty()) name else "$prefix/$name"
		collapseChain(FileNode.Folder(name, path, buildNodes(group.map { (segments, file) -> segments.drop(1) to file }, path)))
	}

	return folders.sortedBy { it.name } + leaves.map { (segments, file) -> FileNode.Leaf(segments.first(), file.path, file) }
}

private fun collapseChain(folder: FileNode.Folder): FileNode.Folder {
	val onlyChild = folder.children.singleOrNull() as? FileNode.Folder ?: return folder

	return collapseChain(FileNode.Folder("${folder.name}/${onlyChild.name}", onlyChild.path, onlyChild.children))
}

/** Picks the icon from the file extension, so a `.mcfunction` never looks like a `.json`. */
@Composable
private fun FileIcon(file: GeneratedFile) = when (file.extension) {
	"json" -> LucideBraces()
	"mcfunction" -> LucideTerminal()
	"mcmeta" -> LucideFileCog()
	"txt", "md" -> LucideFileText()
	else -> LucideFile()
}

@Composable
fun FileTree(files: List<GeneratedFile>, selectedPath: String?, onSelect: (String) -> Unit) {
	val nodes = remember(files) { buildFileTree(files) }
	val collapsed = remember(files) { mutableStateMapOf<String, Boolean>() }

	Div({ classes(PlaygroundStyle.fileTree) }) {
		TreeLevel(nodes, 0, selectedPath, collapsed, onSelect)
	}
}

@Composable
private fun TreeLevel(
	nodes: List<FileNode>,
	depth: Int,
	selectedPath: String?,
	collapsed: MutableMap<String, Boolean>,
	onSelect: (String) -> Unit,
) {
	nodes.forEach { node ->
		when (node) {
			is FileNode.Folder -> {
				val isCollapsed = collapsed[node.path] == true

				Button({
					classes(PlaygroundStyle.treeRow, PlaygroundStyle.treeFolder)
					style { paddingLeft(indentOf(depth)) }
					title(node.path)
					onClick { collapsed[node.path] = !isCollapsed }
				}) {
					Span({ classes(PlaygroundStyle.treeChevron) }) {
						if (isCollapsed) LucideChevronRight() else LucideChevronDown()
					}

					Span({ classes(PlaygroundStyle.treeIcon) }) {
						if (isCollapsed) LucideFolder() else LucideFolderOpen()
					}

					Span({ classes(PlaygroundStyle.treeLabel) }) { Text(node.name) }
				}

				if (!isCollapsed) TreeLevel(node.children, depth + 1, selectedPath, collapsed, onSelect)
			}

			is FileNode.Leaf -> Button({
				classes(*leafClasses(node.path == selectedPath))
				style { paddingLeft(indentOf(depth)) }
				title(node.path)
				onClick { onSelect(node.path) }
			}) {
				Span({ classes(PlaygroundStyle.treeIcon, PlaygroundStyle.treeFileIcon) }) { FileIcon(node.file) }
				Span({ classes(PlaygroundStyle.treeLabel) }) { Text(node.name) }
			}
		}
	}
}

private fun indentOf(depth: Int) = (0.5 + depth * 0.7).cssRem

private fun leafClasses(active: Boolean) = when {
	active -> arrayOf(PlaygroundStyle.treeRow, PlaygroundStyle.treeFile, PlaygroundStyle.treeFileActive)
	else -> arrayOf(PlaygroundStyle.treeRow, PlaygroundStyle.treeFile)
}
