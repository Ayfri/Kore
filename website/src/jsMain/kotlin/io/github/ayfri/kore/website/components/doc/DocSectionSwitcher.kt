package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.docSections
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

@Composable
fun DocSectionSwitcher(current: DocSection) {
	Ul({
		classes(DocTreeStyle.sections)
	}) {
		docSections.forEach { section ->
			Li {
				A(section.firstPage.path, {
					classes(DocTreeStyle.sectionLink)
					if (section.id == current.id) {
						classes(DocTreeStyle.currentSection)
						attr("aria-current", "true")
					}
				}) {
					section.icon()
					Text(section.title)
				}
			}
		}
	}
}
