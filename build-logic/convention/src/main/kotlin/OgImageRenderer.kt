import java.awt.*
import java.awt.font.TextAttribute
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.FileImageOutputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders the 1200x630 Open Graph cards of the website with Java2D, using the site fonts fetched once from Fontsource into [fontsDir].
 * Instances are thread-safe, so cards can be rendered in parallel.
 */
class OgImageRenderer(fontsDir: File, logo: File, private val stars: Int?) {
	private val logoImage = ImageIO.read(logo)
	private val titleFont = loadFont(fontsDir, "sora", 700).deriveFont(68f)
	private val bodyFont = loadFont(fontsDir, "ibm-plex-sans", 400).deriveFont(30f)
	private val badgeFont = bodyFont.deriveFont(26f)
	private val footerFont = bodyFont.deriveFont(24f)
	private val labelFont = loadFont(fontsDir, "jetbrains-mono", 700).deriveFont(mapOf(TextAttribute.SIZE to 22f, TextAttribute.TRACKING to 0.12f))

	fun render(output: File, label: String, title: String, description: String, footer: String) {
		val image = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
		val g = image.createGraphics()
		g.setRenderingHints(
			mapOf(
				RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
				RenderingHints.KEY_FRACTIONALMETRICS to RenderingHints.VALUE_FRACTIONALMETRICS_ON,
				RenderingHints.KEY_INTERPOLATION to RenderingHints.VALUE_INTERPOLATION_BICUBIC,
				RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY,
				RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
			)
		)

		g.color = BACKGROUND
		g.fillRect(0, 0, WIDTH, HEIGHT)
		g.glow(1120f, 40f, 620f, TEAL, 90)
		g.glow(40f, 640f, 480f, YELLOW, 45)

		g.drawImage(logoImage, PADDING, HEADER_Y - LOGO_HEIGHT / 2, logoImage.width * LOGO_HEIGHT / logoImage.height, LOGO_HEIGHT, null)
		stars?.let { g.starsBadge("$it GitHub stars") }

		g.font = titleFont
		val titleLines = g.wrap(title, 3)
		g.font = bodyFont
		val descriptionLines = g.wrap(description, 4 - titleLines.size)

		val blockHeight = 90 + (titleLines.size - 1) * TITLE_LINE + if (descriptionLines.isEmpty()) 0 else 62 + (descriptionLines.size - 1) * BODY_LINE
		var y = CONTENT_TOP + (FOOTER_Y - CONTENT_TOP - 40 - blockHeight) / 2

		g.font = labelFont
		g.color = LABEL
		g.drawString(label.uppercase(), PADDING, y)

		y += 90 - TITLE_LINE
		g.font = titleFont
		g.color = Color.WHITE
		for (line in titleLines) {
			y += TITLE_LINE
			g.drawString(line, PADDING, y)
		}

		y += 62 - BODY_LINE
		g.font = bodyFont
		g.color = MUTED
		for (line in descriptionLines) {
			y += BODY_LINE
			g.drawString(line, PADDING, y)
		}

		g.font = footerFont
		g.color = BORDER
		g.drawString(footer, PADDING, FOOTER_Y)

		g.paint = GradientPaint(0f, 0f, YELLOW, WIDTH.toFloat(), 0f, TEAL)
		g.fillRect(0, HEIGHT - 10, WIDTH, 10)
		g.dispose()

		output.parentFile.mkdirs()
		output.delete()
		/** Quality 0 maps to deflate level 9, ~30% lighter than `ImageIO.write`'s default level, JPEG gains less and bands the glows. */
		val writer = ImageIO.getImageWritersByFormatName("png").next()
		val params = writer.defaultWriteParam.apply {
			compressionMode = ImageWriteParam.MODE_EXPLICIT
			compressionQuality = 0f
		}
		FileImageOutputStream(output).use {
			writer.output = it
			writer.write(null, IIOImage(image, null, null), params)
		}
		writer.dispose()
	}

	private fun Graphics2D.glow(x: Float, y: Float, radius: Float, color: Color, alpha: Int) {
		paint = RadialGradientPaint(
			Point2D.Float(x, y), radius, floatArrayOf(0f, 1f),
			arrayOf(Color(color.red, color.green, color.blue, alpha), Color(color.red, color.green, color.blue, 0)),
		)
		fillRect(0, 0, WIDTH, HEIGHT)
	}

	/** Right-aligned pill facing the logo, holding a five-pointed star and [text]. */
	private fun Graphics2D.starsBadge(text: String) {
		font = badgeFont
		val iconRadius = 12.0
		val height = 54
		val width = 22 * 2 + iconRadius.toInt() * 2 + 12 + fontMetrics.stringWidth(text)
		val x = WIDTH - PADDING - width
		val top = HEADER_Y - height / 2

		color = Color(255, 255, 255, 12)
		fillRoundRect(x, top, width, height, height, height)
		color = Color(255, 255, 255, 36)
		drawRoundRect(x, top, width, height, height, height)

		val star = Path2D.Double()
		val centerX = x + 22 + iconRadius
		for (point in 0..<10) {
			val radius = if (point % 2 == 0) iconRadius else iconRadius * 0.45
			val angle = PI / 5 * point - PI / 2
			val (px, py) = centerX + radius * cos(angle) to HEADER_Y + radius * sin(angle)
			if (point == 0) star.moveTo(px, py) else star.lineTo(px, py)
		}
		star.closePath()
		color = YELLOW
		fill(star)

		color = Color.WHITE
		drawString(text, x + 22 + iconRadius.toInt() * 2 + 12, HEADER_Y + (fontMetrics.ascent - fontMetrics.descent) / 2)
	}

	/** Greedy word wrap within the content width, the last kept line ends with an ellipsis when [text] doesn't fit in [maxLines]. */
	private fun Graphics2D.wrap(text: String, maxLines: Int): List<String> {
		val metrics = fontMetrics
		val maxWidth = WIDTH - PADDING * 2
		val lines = mutableListOf<String>()
		var current = ""
		for (word in text.split(' ').filter(String::isNotEmpty)) {
			val candidate = if (current.isEmpty()) word else "$current $word"
			if (metrics.stringWidth(candidate) <= maxWidth || current.isEmpty()) current = candidate
			else {
				lines += current
				current = word
			}
		}
		if (current.isNotEmpty()) lines += current
		if (lines.size <= maxLines) return lines
		if (maxLines < 1) return emptyList()

		val kept = lines.take(maxLines).toMutableList()
		var last = kept.last()
		while (metrics.stringWidth("$last…") > maxWidth && ' ' in last) last = last.substringBeforeLast(' ')
		kept[kept.lastIndex] = "${last.trimEnd(',', '.', ';', ':', '-')}…"
		return kept
	}

	private companion object {
		const val BODY_LINE = 42
		const val CONTENT_TOP = 170
		const val FOOTER_Y = 572
		const val HEADER_Y = 96
		const val HEIGHT = 630
		const val LOGO_HEIGHT = 78
		const val PADDING = 80
		const val TITLE_LINE = 76
		const val WIDTH = 1200

		val BACKGROUND = Color(0x181a1f)
		val BORDER = Color(0x8c9ab1)
		val LABEL = Color(0x23cae8)
		val MUTED = Color(0xa7b5bd)
		val TEAL = Color(0x049bb2)
		val YELLOW = Color(0xfec907)

		fun loadFont(dir: File, family: String, weight: Int): Font {
			val file = dir.resolve("$family-$weight.ttf")
			if (!file.exists()) {
				dir.mkdirs()
				val temp = File.createTempFile(family, ".ttf", dir)
				URI("https://cdn.jsdelivr.net/fontsource/fonts/$family@latest/latin-$weight-normal.ttf").toURL().openStream().use { input ->
					temp.outputStream().use(input::copyTo)
				}
				temp.renameTo(file)
			}
			return Font.createFont(Font.TRUETYPE_FONT, file)
		}
	}
}
