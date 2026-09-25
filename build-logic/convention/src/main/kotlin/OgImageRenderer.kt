import java.awt.*
import java.awt.font.TextAttribute
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import javax.imageio.ImageIO

/**
 * Renders the 1200x630 Open Graph cards of the website with Java2D, using the site fonts fetched once from Fontsource into [fontsDir].
 * Instances are thread-safe, so cards can be rendered in parallel.
 */
class OgImageRenderer(fontsDir: File, logo: File) {
	private val logoImage = ImageIO.read(logo)
	private val titleFont = loadFont(fontsDir, "sora", 700).deriveFont(68f)
	private val bodyFont = loadFont(fontsDir, "ibm-plex-sans", 400).deriveFont(30f)
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

		g.color = Color(255, 255, 255, 9)
		for (x in 0..WIDTH step GRID) g.drawLine(x, 0, x, HEIGHT)
		for (y in 0..HEIGHT step GRID) g.drawLine(0, y, WIDTH, y)

		val logoHeight = 60
		g.drawImage(logoImage, PADDING, 64, logoImage.width * logoHeight / logoImage.height, logoHeight, null)

		g.font = titleFont
		val titleLines = g.wrap(title, 3)
		g.font = bodyFont
		val descriptionLines = g.wrap(description, 4 - titleLines.size)

		val blockHeight = 90 + (titleLines.size - 1) * TITLE_LINE + if (descriptionLines.isEmpty()) 0 else 62 + (descriptionLines.size - 1) * BODY_LINE
		var y = 150 + (FOOTER_Y - 150 - 40 - blockHeight) / 2

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
		ImageIO.write(image, "png", output)
	}

	private fun Graphics2D.glow(x: Float, y: Float, radius: Float, color: Color, alpha: Int) {
		paint = RadialGradientPaint(
			Point2D.Float(x, y), radius, floatArrayOf(0f, 1f),
			arrayOf(Color(color.red, color.green, color.blue, alpha), Color(color.red, color.green, color.blue, 0)),
		)
		fillRect(0, 0, WIDTH, HEIGHT)
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
		const val FOOTER_Y = 572
		const val GRID = 40
		const val HEIGHT = 630
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
