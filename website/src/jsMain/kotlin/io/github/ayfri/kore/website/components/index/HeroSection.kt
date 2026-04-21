package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

enum class HeroTab(val tabName: String, val language: String) {
	INDEX("index.kt", "kotlin") {
		override val code = """
			fun main() {
				val datapack = dataPack("test") {
					function("display_text") {
						tellraw(allPlayers(), textComponent("Hello World!"))
					}

					function("tp_random_entity_to_entity") {
						val target = allEntities(limitToOne = true) {
							sort = Sort.RANDOM
						}

						execute {
							asTarget(target)
							run {
								summon(Entities.CREEPER, vec3())
							}
						}
					}

					pack {
						description = textComponent("Datapack test for ", Color.GOLD) + text("Kore", Color.AQUA) {
							bold = true
						}
					}
				}

				datapack.generateZip()
			}
		""".trimIndent()
	},
	CUSTOM_SCOREBOARD("customScoreboard.kt", "kotlin") {
		override val code = """
			fun scoreboardDisplayDatapack() = dataPack("scoreboard_display") {
				val randomEntityUUID = randomUUID()
				val scoreName = "score"

				val scoreHolderEntity = allEntities(limitToOne = true) {
					nbt = nbt {
						putNbtCompound("data") {
							this["UUID"] = randomEntityUUID.uuid.toString()
						}
					}
				}

				load {
					ScoreboardDisplay.resetAll()

					summon(EntityTypes.MARKER) {
						putNbtCompound("data") {
							this["UUID"] = randomEntityUUID.uuid.toString()
						}
					}

					scoreboard.objectives.add(scoreName)
					scoreboard.player(scoreHolderEntity) {
						set(scoreName, 0)
					}
				}

				tick {
					scoreboardDisplay("minigame") {
						displayName = textComponent("✪ Mini-game ✪", Color.GOLD)
						appendLine("Game: Mini-game")
						appendLine("IP: 127.0.0.1")
						appendLine("Players: 10/20")
						appendLine(textComponent("Score: ")) {
							customScoreEntity = scoreHolderEntity
							customScoreObjective = scoreName
						}

						emptyLine()
						appendLine("HyKore server 3.1.0")

						hideValues(1..<4)
						hideValues(6..lines.size)
					}
				}
			}
		""".trimIndent()
	},
	EXTERNAL_DATAPACK("externalDatapack.kt", "kotlin") {
		override val code = """
			import io.github.ayfri.kore.bindings.api.importDatapacks
			import kore.dependencies.vanillatweaks.VanillaTweaks

			fun setupBindings() = importDatapacks {
				github("VanillaTweaks/Vanilla-Tweaks-Datapacks")
			}

			fun main() {
				setupBindings()

				val datapack = dataPack("my_enhanced_datapack") {
					function("spawn_at_valid_location") {
						execute {
							ifCondition {
								block(vec3(0, -1, 0), VanillaTweaks.Spawn.Tags.Blocks.VALID_SPAWN_LOCATION)
							}
							run {
								say("Spawning at valid location!")
							}
						}
					}
				}

				datapack.generateZip()
			}
		""".trimIndent()
	};

	abstract val code: String
}

@Composable
fun HeroSection() {
	Style(HeroSectionStyle)

	Section({
		classes(HeroSectionStyle.heroSection)
	}) {
		Div({
			classes(HeroSectionStyle.heroGrid)
		}) {
			Div({
				classes(HeroSectionStyle.heroCopy)
			}) {
				H1({
					classes(HeroSectionStyle.title)
				}) {
					Span({
						classes(HeroSectionStyle.titleText)
					}) {
						Text("Rethink your datapack development experience with")
					}
					Img {
						attr("src", "/logo.png")
						classes(HeroSectionStyle.logo)
					}
				}

				Div({
					classes(HeroSectionStyle.versionContainer)
				}) {
					Div({ classes(HeroSectionStyle.versionItem) }) {
						Span("Kore", classes = arrayOf(HeroSectionStyle.versionLabel))
						Span(AppGlobals["projectVersion"] ?: "1.37.0", classes = arrayOf(HeroSectionStyle.versionValue))
					}
					Div({ classes(HeroSectionStyle.versionDivider) })
					Div({ classes(HeroSectionStyle.versionItem) }) {
						Span("Minecraft", classes = arrayOf(HeroSectionStyle.versionLabel))
						Span(
							AppGlobals["minecraftVersion"] ?: "1.21.10",
							classes = arrayOf(HeroSectionStyle.versionValue)
						)
					}
				}

				P(
					"""
						A modern, type-safe Kotlin library for Minecraft datapack development.
						Create complex datapacks without ever writing JSON or MCFunction manually.
					""".trimIndent(),
					HeroSectionStyle.subTitle
				)

				Div({
					classes(HeroSectionStyle.actions)
				}) {
					LinkButton("Get Started", "/docs/getting-started", color = ButtonColor.PRIMARY)
					LinkButton("GitHub", GITHUB_LINK, variant = ButtonVariant.OUTLINE)
				}
			}

			Div({
				classes(HeroSectionStyle.heroPanel)
			}) {
				val tabs = HeroTab.entries.map {
					Tab(it.tabName) { CodeBlock(it.code, it.language) }
				}

				Tabs(tabs, className = HeroSectionStyle.tabs, contentClassName = HeroSectionStyle.tabContent)
			}
		}
	}
}

object HeroSectionStyle : StyleSheet() {
	init {
		smMax(child(className("code-toolbar"), type("pre"))) {
			fontSize(0.8.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val heroReveal by keyframes {
		from {
			opacity(0)
			transform { translateY(16.px) }
		}
		to {
			opacity(1)
			transform { translateY(0.px) }
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val panelFloat by keyframes {
		from { transform { translateY(0.px) } }
		to { transform { translateY((-12).px) } }
	}

	val heroSection by style {
		boxSizing(BoxSizing.BorderBox)
		position(Position.Relative)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		paddingTop(4.2.cssRem)
		paddingBottom(2.6.cssRem)
		paddingX(6.vw)
		property(
			"background",
			"radial-gradient(circle at 15% 15%, rgba(8, 182, 214, 0.18) 0%, transparent 42%), " +
				"radial-gradient(circle at 85% 20%, rgba(254, 201, 7, 0.12) 0%, transparent 38%), " +
				"linear-gradient(180deg, rgba(15, 20, 27, 0.98) 0%, rgba(15, 20, 27, 0.85) 65%, rgba(15, 20, 27, 0.6) 100%)"
		)
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		overflow(Overflow.Hidden)

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.05.cssRem)
		}

		mdMax(self) {
			"p" style {
				fontSize(1.cssRem)
			}
		}

		smMax(self) {
			paddingTop(3.2.cssRem)
			paddingBottom(2.cssRem)
			paddingX(1.1.cssRem)
		}
	}

	val heroGrid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(0, 1.05fr) minmax(0, 0.95fr)")
		gap(2.4.cssRem)
		alignItems(AlignItems.Start)
		minWidth(0.px)

		lgMax(self) {
			gridTemplateColumns("1fr")
			gap(2.2.cssRem)
		}
	}

	val heroCopy by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.FlexStart)
		gap(1.2.cssRem)
		minWidth(0.px)
		textAlign(TextAlign.Left)

		lgMin {
			marginTop(4.cssRem)
		}

		lgMax(self) {
			alignItems(AlignItems.Center)
			textAlign(TextAlign.Center)
		}
	}

	val heroPanel by style {
		backgroundColor(Color("var(--landing-card)"))
		borderRadius(1.6.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		minWidth(0.px)
		maxWidth(100.percent)
		overflow(Overflow.Hidden)
		padding(0.65.cssRem)
		boxShadow(0.px, 30.px, 80.px, 0.px, rgba(5, 14, 23, 0.55))
		property("backdrop-filter", "blur(14px)")
		animation(panelFloat) {
			duration(8.s)
			timingFunction(AnimationTimingFunction.EaseInOut)
			direction(AnimationDirection.Alternate)
		}
		property("animation-iteration-count", "infinite")

		mdMax(self) {
			padding(0.45.cssRem)
		}
	}

	val title by style {
		fontSize(3.2.cssRem)
		fontWeight(FontWeight.Bold)
		letterSpacing((-2).px)
		lineHeight(1.05.number)
		margin(0.px)
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem)
		textTransform(TextTransform.Uppercase)
		animation(heroReveal) {
			duration(0.7.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}

		mdMax(self) {
			fontSize(2.35.cssRem)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			gap(0.85.cssRem)
			textAlign(TextAlign.Center)
		}

		xsMax(self) {
			fontSize(1.85.cssRem)
			letterSpacing((-1).px)
		}
	}

	val titleText by style {
		display(DisplayStyle.InlineBlock)
		maxWidth(100.percent)
		overflowWrap(OverflowWrap.Anywhere)
		property("text-wrap", "balance")
		textGradient(GlobalStyle.logoRightColor, GlobalStyle.logoLeftColor)
		property("text-shadow", "0 10px 30px rgba(4, 155, 178, 0.2)")
	}

	val logo by style {
		height(4.1.cssRem)
		marginLeft(0.cssRem)
		property("filter", "drop-shadow(0 16px 24px rgba(4, 155, 178, 0.35))")
		flexShrink(0)
		verticalAlign(VerticalAlign.Middle)

		mdMax(self) {
			height(3.2.cssRem)
			marginLeft(0.cssRem)
		}

		xsMax(self) {
			height(2.65.cssRem)
		}
	}

	val subTitle by style {
		maxWidth(38.cssRem)
		margin(0.px)
		color(Color("var(--landing-muted)"))

		mdMax(self) {
			maxWidth(92.percent)
			textAlign(TextAlign.Center)
			marginX(auto)
		}
	}

	val actions by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.2.cssRem)
		flexWrap(FlexWrap.Wrap)

		mdMax(child(self, type("a"))) {
			fontSize(1.1.cssRem)
		}

		mdMax(self) {
			justifyContent(JustifyContent.Center)
		}
	}

	val tabs by style {
		width(100.percent)
		minHeight(18.cssRem)
		height(auto)
		borderRadius(1.2.cssRem)

		xlMax(self) {
			minHeight(17.cssRem)
		}

		mdMax(self) {
			minHeight(16.cssRem)
		}
	}

	val tabContent by style {
		backgroundColor(Color("var(--landing-surface-2)"))
		height(auto)
		minWidth(0.px)
		width(100.percent)

		"pre" style {
			fontSize(0.95.cssRem)
			margin(0.px)
			maxWidth(100.percent)
			overflowX(Overflow.Auto)
		}

		smMax(self) {
			"pre" style {
				fontSize(0.7.cssRem)
			}
		}
	}

	val versionContainer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.Center)
		backgroundColor(Color("rgba(21, 28, 38, 0.8)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		maxWidth(100.percent)
		padding(0.55.cssRem, 1.6.cssRem)
		gap(1.2.cssRem)

		smMax(self) {
			borderRadius(1.05.cssRem)
			flexWrap(FlexWrap.Nowrap)
			gap(0.65.cssRem)
			justifyContent(JustifyContent.SpaceBetween)
			padding(0.55.cssRem, 0.9.cssRem)
			width(100.percent)
		}
	}

	val versionItem by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.5.cssRem)
		alignItems(AlignItems.Center)
		flexWrap(FlexWrap.Nowrap)
		minWidth(0.px)

		smMax(self) {
			gap(0.35.cssRem)
		}
	}

	val versionLabel by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.82.cssRem)
		fontWeight(FontWeight.Medium)
		fontFamily("JetBrains Mono", "IBM Plex Mono", "Consolas", "monospace")
		textTransform(TextTransform.Uppercase)
		letterSpacing(1.4.px)

		xsMax(self) {
			fontSize(0.68.cssRem)
			letterSpacing(0.8.px)
		}
	}

	val versionValue by style {
		color(Color("var(--landing-accent)"))
		fontSize(1.cssRem)
		fontFamily("JetBrains Mono", "IBM Plex Mono", "Consolas", "monospace")
		fontWeight(FontWeight.Bold)

		xsMax(self) {
			fontSize(0.82.cssRem)
		}
	}

	val versionDivider by style {
		width(1.px)
		height(1.cssRem)
		backgroundColor(Color("var(--landing-border)"))
	}
}
