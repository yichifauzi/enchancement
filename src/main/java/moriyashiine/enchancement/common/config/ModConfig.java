package moriyashiine.enchancement.common.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import moriyashiine.enchancement.common.Enchancement;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Stream;

@Config(name = Enchancement.MOD_ID)
public class ModConfig implements ConfigData {
	@ConfigEntry.Gui.RequiresRestart
	public List<Identifier> allowedEnchantments = Stream.of("enchancement:assimilation", "enchancement:buffet", "enchancement:perception",
			"enchancement:amphibious", "enchancement:wardenspine",
			"enchancement:dash", "enchancement:slide",
			"enchancement:acceleration", "enchancement:bouncy", "enchancement:gale",
			"minecraft:fire_aspect", "enchancement:frostbite",
			"enchancement:chaos", "enchancement:delay",
			"minecraft:riptide", "enchancement:warp",
			"enchancement:molten",
			"enchancement:beheading", "enchancement:lumberjack",
			"enchancement:bury", "enchancement:scooping",
			"enchancement:disarm", "enchancement:grapple", "minecraft:luck_of_the_sea",
			"minecraft:efficiency", "minecraft:silk_touch", "minecraft:unbreaking").map(Identifier::tryParse).toList();

	@ConfigEntry.Gui.RequiresRestart
	public boolean singleEnchantmentMode = true;
	@ConfigEntry.Gui.RequiresRestart
	public boolean singleLevelMode = true;

	public boolean allTridentsHaveLoyalty = true;
	public boolean luckOfTheSeaHasLure = true;

	public int fireAspectIgnitionLevel = 1;
	public int maxLumberjackBlocks = 1024;
	@ConfigEntry.Gui.RequiresRestart
	public int fishingRodDurability = 256;
	public int unbreakableUnbreakingLevel = 1;
}
