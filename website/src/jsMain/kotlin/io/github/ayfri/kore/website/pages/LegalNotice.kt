package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

private const val CONTACT_EMAIL = "pierre.ayfri@gmail.com"

@Composable
private fun ExternalLink(href: String, text: String) = A(href, { target(ATarget.Blank) }) { Text(text) }

@Page
@Composable
fun LegalNoticePage() {
	Style(LegalNoticeStyle)

	PageLayout("Legal Notice & Privacy") {
		setDescription("Legal notice and privacy information for kore.ayfri.com, the website of Kore, the open-source Kotlin datapack generator for Minecraft.")

		Article({ classes(LegalNoticeStyle.page) }) {
			H1 { Text("Legal Notice & Privacy") }
			P({ classes(LegalNoticeStyle.updated) }) { Text("Last updated: September 25, 2026") }

			H2 { Text("Publisher") }
			P {
				Text("This website is published by Pierre Roy (Ayfri), a private individual, as part of the non-commercial open-source project Kore. ")
				Text("Pierre Roy is also the publication director. Contact: ")
				A("mailto:$CONTACT_EMAIL") { Text(CONTACT_EMAIL) }
				Text(".")
			}

			H2 { Text("Hosting") }
			P {
				Text("Cloudflare, Inc., 101 Townsend Street, San Francisco, CA 94107, USA, +1 888 993 5273, ")
				ExternalLink("https://www.cloudflare.com", "cloudflare.com")
				Text(".")
			}

			H2 { Text("Intellectual property") }
			P {
				Text("Kore and the source code of this website are released under the ")
				ExternalLink("$GITHUB_LINK/blob/master/LICENSE", "GPL-3.0 License")
				Text(". Minecraft is a trademark of Mojang Synergies AB. Kore is not an official Minecraft product and is not approved by or associated with Mojang or Microsoft.")
			}

			H2 { Text("Personal data & cookies") }
			P { Text("The site has no accounts and no forms. It still processes some technical data through these third parties:") }
			Ul {
				Li {
					B { Text("Google Analytics") }
					Text(" (Google Ireland Ltd.) measures the audience with cookies kept up to 13 months. ")
					ExternalLink("https://policies.google.com/privacy", "Google privacy policy")
					Text(".")
				}
				Li {
					B { Text("Google Fonts") }
					Text(" serves the site fonts, so your browser sends your IP address to Google when loading them.")
				}
				Li {
					B { Text("Cloudflare") }
					Text(" logs requests (IP address, user agent, requested page) to serve and secure the site. ")
					ExternalLink("https://www.cloudflare.com/privacypolicy/", "Cloudflare privacy policy")
					Text(".")
				}
			}
			P {
				Text("Display preferences, like the release filters of the Updates page, stay in your browser's local storage and are never sent anywhere. ")
				Text("Under the GDPR you can access, correct or delete your data by writing to ")
				A("mailto:$CONTACT_EMAIL") { Text(CONTACT_EMAIL) }
				Text(", and file a complaint with the ")
				ExternalLink("https://www.cnil.fr/en", "CNIL")
				Text(".")
			}
		}
	}
}

object LegalNoticeStyle : StyleSheet() {
	val page by style {
		boxSizing(BoxSizing.BorderBox)
		color(GlobalStyle.altTextColor)
		lineHeight(1.7.number)
		marginX(auto)
		maxWidth(48.cssRem)
		padding(3.cssRem, 1.25.cssRem, 4.cssRem)

		"a" style {
			color(GlobalStyle.linkColor)
			transition(0.15.s, "color")

			hover(self) style {
				color(GlobalStyle.linkColorHover)
			}
		}

		"b" style {
			color(GlobalStyle.textColor)
		}

		"h1" style {
			color(GlobalStyle.textColor)
			fontSize(2.25.cssRem)
			margin(0.px)
		}

		"h2" style {
			color(GlobalStyle.textColor)
			fontSize(1.25.cssRem)
			margin(2.cssRem, 0.px, 0.5.cssRem)
		}

		"li" style {
			marginY(0.4.cssRem)
		}
	}

	val updated by style {
		fontSize(0.9.cssRem)
		marginTop(0.25.cssRem)
	}
}
