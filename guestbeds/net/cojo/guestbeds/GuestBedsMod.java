package guestbeds.net.cojo.guestbeds;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@NetworkMod(clientSideRequired = true, serverSideRequired = false)
@Mod(modid = "guestbeds", name = "Guest Beds Mod", version = "v0.1")
public class GuestBedsMod {

	/** Block id of the bed block */
	public static int bedBlockID = 2000;

	/** Item id of the bed item */
	public static int bedItemID = 14000;

	/** ID of the mod */
	public static String MODID = "guestbeds";

	/** Static instance of the mod class */
	@Instance("guestbeds")
	public static GuestBedsMod instance;

	/** The actual guest bed block */
	public static Block guestBedBlock;

	/** Guest bed item */
	public static Item guestBedItem;

	/** Instance of the config */
	public static Configuration config;

	/**
	 * Set the ids in the configs
	 * @param event
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());

		try {
			config.load();

			bedBlockID = config.getBlock("Guest Bed Block ID", bedBlockID).getInt(2000);

			bedItemID = config.getItem("Guest Bed Item ID", bedItemID).getInt(14000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			config.save();
		}
	}

	/**
	 * Initialize the blocks, items, tick handler
	 * @param evt
	 */
	@EventHandler
	public void load(FMLInitializationEvent evt) {
		guestBedBlock = new BlockGuestBed(bedBlockID).setHardness(0.2F).setUnlocalizedName("guest_bed").setTextureName("bed");

		guestBedItem = (new ItemGuestBed(bedItemID)).setMaxStackSize(1).setUnlocalizedName("guest_bed").setTextureName("bed");

		GameRegistry.registerBlock(guestBedBlock, "Guest Bed Block");
		LanguageRegistry.addName(guestBedBlock, "Guest Bed");

		GameRegistry.registerItem(guestBedItem, "Guest Bed Item");
		LanguageRegistry.addName(guestBedItem, "Guest Bed");

		TickRegistry.registerTickHandler(new TickHandlerSleep(), Side.SERVER);
	}

}
