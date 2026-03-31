package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.dimension.generator.FlatGeneratorSettings
import io.github.ayfri.kore.features.worldgen.dimension.generator.Layer
import io.github.ayfri.kore.features.worldgen.flatlevelgeneratorpreset.FlatLevelGeneratorPreset
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.StructureSets
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun deserializersTests() {
	namespacedPolymorphicDeserializer()
	namespacedPolymorphicDeserializerWithMoveIntoProperty()
	inlineAutoDeserializer()
	argumentDeserializer()
	generatorFeatureDeserializer()
}

private inline fun <reified T> Any.assertsIsA(): T {
	(this is T) assertsIs true
	return this as T
}

// region NamespacedPolymorphicSerializer

@Serializable(with = Animal.Companion.AnimalSerializer::class)
private sealed class Animal {
	companion object {
		data object AnimalSerializer : NamespacedPolymorphicSerializer<Animal>(Animal::class)
	}
}

@Serializable
@SerialName("cat")
private data class Cat(val lives: Int = 9) : Animal()

@Serializable
@SerialName("dog")
private data class Dog(val tricks: Int = 0) : Animal()

fun namespacedPolymorphicDeserializer() {
	// JSON deserialization
	val decodedCat =
		json.decodeFromString(Animal.serializer(), """{"type": "minecraft:cat", "lives": 7}""").assertsIsA<Cat>()
	decodedCat.lives assertsIs 7

	val decodedDog =
		json.decodeFromString(Animal.serializer(), """{"type": "minecraft:dog", "tricks": 3}""").assertsIsA<Dog>()
	decodedDog.tricks assertsIs 3

	val decodedDefaultCat =
		json.decodeFromString(Animal.serializer(), """{"type": "minecraft:cat"}""").assertsIsA<Cat>()
	decodedDefaultCat.lives assertsIs 9

	// SNBT deserialization
	val decodedCatSnbt =
		snbt.decodeFromString(Animal.serializer(), """{type: "minecraft:cat", lives: 7}""").assertsIsA<Cat>()
	decodedCatSnbt.lives assertsIs 7

	val decodedDogSnbt =
		snbt.decodeFromString(Animal.serializer(), """{type: "minecraft:dog", tricks: 3}""").assertsIsA<Dog>()
	decodedDogSnbt.tricks assertsIs 3

	// Roundtrip
	val cat = Cat(lives = 5)
	val catJson = json.encodeToString(Animal.serializer(), cat)
	val catDecoded = json.decodeFromString(Animal.serializer(), catJson).assertsIsA<Cat>()
	catDecoded.lives assertsIs 5
}

// endregion

// region NamespacedPolymorphicSerializer with moveIntoProperty

@Serializable(with = Shape.Companion.ShapeSerializer::class)
private sealed class Shape {
	companion object {
		data object ShapeSerializer : NamespacedPolymorphicSerializer<Shape>(Shape::class, moveIntoProperty = "value")
	}
}

@Serializable
@SerialName("circle")
private data class Circle(val radius: Int = 1) : Shape()

@Serializable
@SerialName("square")
private data class Square(val side: Int = 1) : Shape()

fun namespacedPolymorphicDeserializerWithMoveIntoProperty() {
	// JSON deserialization with moveIntoProperty
	val decodedCircle =
		json.decodeFromString(Shape.serializer(), """{"type": "minecraft:circle", "value": {"radius": 5}}""")
			.assertsIsA<Circle>()
	decodedCircle.radius assertsIs 5

	val decodedSquare =
		json.decodeFromString(Shape.serializer(), """{"type": "minecraft:square", "value": {"side": 3}}""")
			.assertsIsA<Square>()
	decodedSquare.side assertsIs 3

	// Empty content (skipEmptyOutput)
	val decodedDefault =
		json.decodeFromString(Shape.serializer(), """{"type": "minecraft:circle"}""").assertsIsA<Circle>()
	decodedDefault.radius assertsIs 1

	// SNBT deserialization with moveIntoProperty
	val decodedCircleSnbt =
		snbt.decodeFromString(Shape.serializer(), """{type: "minecraft:circle", value: {radius: 5}}""")
			.assertsIsA<Circle>()
	decodedCircleSnbt.radius assertsIs 5

	// Roundtrip
	val circle = Circle(radius = 10)
	val circleJson = json.encodeToString(Shape.serializer(), circle)
	val circleDecoded = json.decodeFromString(Shape.serializer(), circleJson).assertsIsA<Circle>()
	circleDecoded.radius assertsIs 10
}

// endregion

// region InlineAutoSerializer

@Serializable(with = Wrapper.Companion.WrapperSerializer::class)
private data class Wrapper(var content: String = "") {
	companion object {
		data object WrapperSerializer : InlineAutoSerializer<Wrapper>(Wrapper::class)
	}
}

@Serializable(with = IntWrapper.Companion.IntWrapperSerializer::class)
private data class IntWrapper(var value: Int = 0) {
	companion object {
		data object IntWrapperSerializer : InlineAutoSerializer<IntWrapper>(IntWrapper::class)
	}
}

fun inlineAutoDeserializer() {
	// JSON deserialization
	val decodedWrapper = json.decodeFromString(Wrapper.serializer(), "\"hello\"")
	decodedWrapper.content assertsIs "hello"

	val decodedIntWrapper = json.decodeFromString(IntWrapper.serializer(), "42")
	decodedIntWrapper.value assertsIs 42

	// SNBT deserialization
	val decodedWrapperSnbt = snbt.decodeFromString(Wrapper.serializer(), "\"hello\"")
	decodedWrapperSnbt.content assertsIs "hello"

	val decodedIntWrapperSnbt = snbt.decodeFromString(IntWrapper.serializer(), "42")
	decodedIntWrapperSnbt.value assertsIs 42

	// Roundtrip
	val wrapper = Wrapper("world")
	val wrapperJson = json.encodeToString(Wrapper.serializer(), wrapper)
	val wrapperDecoded = json.decodeFromString(Wrapper.serializer(), wrapperJson)
	wrapperDecoded.content assertsIs "world"

	val intWrapper = IntWrapper(99)
	val intWrapperJson = json.encodeToString(IntWrapper.serializer(), intWrapper)
	val intWrapperDecoded = json.decodeFromString(IntWrapper.serializer(), intWrapperJson)
	intWrapperDecoded.value assertsIs 99
}

fun argumentDeserializer() {
	json.decodeFromString(Argument.serializer(), "\"oak_stairs\"").asString() assertsIs "minecraft:oak_stairs"

	val decodedItem = json.decodeFromString(Argument.serializer(), "\"minecraft:diamond_sword[minecraft:damage=4]\"")
		.assertsIsA<ItemArgument>()
	decodedItem.asString() assertsIs "minecraft:diamond_sword[minecraft:damage=4]"
	(decodedItem.components?.toString() ?: "") assertsIs "[minecraft:damage=4]"
	decodedItem.components = null
	decodedItem.asString() assertsIs "minecraft:diamond_sword"

	val decodedBlock =
		json.decodeFromString(Argument.serializer(), "\"minecraft:oak_stairs[facing=north,half=top]{powered:1b}\"")
			.assertsIsA<BlockArgument>()
	decodedBlock.asString() assertsIs "minecraft:oak_stairs[facing=north,half=top]{powered:1b}"
	(decodedBlock.nbtData?.toString() ?: "") assertsIs "{powered:1b}"
	decodedBlock.states["shape"] = "straight"
	decodedBlock.asString() assertsIs "minecraft:oak_stairs[facing=north,half=top,shape=straight]{powered:1b}"
	decodedBlock.nbtData = null
	decodedBlock.asString() assertsIs "minecraft:oak_stairs[facing=north,half=top,shape=straight]"
}

// endregion

fun generatorFeatureDeserializer() {
	val generator = FlatLevelGeneratorPreset(
		fileName = "rich_flat_level_generator_preset",
		display = Items.DIAMOND_SWORD {
			damage(4)
		},
		settings = FlatGeneratorSettings(
			biome = Biomes.PLAINS,
			layers = listOf(
				Layer(
					Blocks.OAK_STAIRS(
						states = mapOf(
							"half" to "top",
							"facing" to "north",
						),
					) {
						this["powered"] = true
					},
					height = 2,
				),
			),
			structureOverrides = listOf(StructureSets.VILLAGES),
		),
	)

	val encoded = json.encodeToString(FlatLevelGeneratorPreset.serializer(), generator)
	val decoded = json.decodeFromString(FlatLevelGeneratorPreset.serializer(), encoded)
	val decodedLayer = decoded.settings.layers.single()
	val expectedLayer = generator.settings.layers.single()

	decoded.display.asString() assertsIs generator.display.asString()
	(decoded.display.components?.toString() ?: "") assertsIs (generator.display.components?.toString() ?: "")
	decodedLayer.block.asString() assertsIs expectedLayer.block.asString()
	decodedLayer.block.states.toString() assertsIs expectedLayer.block.states.toString()
	(decodedLayer.block.nbtData?.toString() ?: "") assertsIs (expectedLayer.block.nbtData?.toString() ?: "")
	decoded.settings.structureOverrides.single().asString() assertsIs StructureSets.VILLAGES.asString()
}
