package lonefox.muhcannons

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

object MuhcannonsDataGenerator : DataGeneratorEntrypoint {
	private val projectiles = TagKey.of(RegistryKeys.ITEM, Identifier("muh-cannons:cannon_ball"));

	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()

		pack.addProvider(::TagGenerator)
	}

	private class TagGenerator(
		output: FabricDataOutput?,
		completableFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
	) : ItemTagProvider(output, completableFuture) {
		override fun configure(arg: RegistryWrapper.WrapperLookup?) {
			getOrCreateTagBuilder(projectiles)
				.add(Identifier("muh-cannons:cannon_ball"))
		}

	}
}