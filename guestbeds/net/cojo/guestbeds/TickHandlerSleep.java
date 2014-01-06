package guestbeds.net.cojo.guestbeds;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerSleep implements ITickHandler {

	/** Map of player entity ids to their spawn locations */
	public static HashMap<Integer, ChunkCoordinates> playerCoordsMap = new HashMap<Integer, ChunkCoordinates>();

	/** Are all players in the last stage of sleep? */
	private static boolean areAllPlayersAsleep;

	/** Default constructor */
	public TickHandlerSleep() {
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.WORLD))) {
			onPreSleepTick(tickData[0]);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.WORLD))) {
			onPostSleepTick(tickData[0]);
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() { 
		return null;
	}

	/**
	 * Called right before a player starts sleeping, save spawn coords
	 */
	public void onPreSleepTick(Object obj) {
		if (obj instanceof WorldServer) {
			WorldServer world = (WorldServer)obj;
			// Are all players in the last stage of sleep?
			this.areAllPlayersAsleep = world.areAllPlayersAsleep();

			updatePlayerMap(world);
		}
	}

	/**
	 * Update the player map, removing stale players (players who are no longer in a guest bed)
	 * @param world World instance
	 */
	private static void updatePlayerMap(WorldServer world) {
		Iterator<Integer> keys = playerCoordsMap.keySet().iterator(); 

		while (keys.hasNext()) {
			Entity player = world.getEntityByID(keys.next());
			
			// If player is not in guest bed, remove them from the map
			if (world.getBlockId((int)player.posX, (int)player.posY, (int)player.posZ) != GuestBedsMod.bedBlockID) {
				playerCoordsMap.remove(player.entityId);
			}
		}
	}

	/**
	 * Simplification method for storing entity spawn coords
	 * @param entityID Unique id of the entity
	 * @param spawnCoords Coordinates of spawn
	 */
	public static void saveCoords(Integer entityID, ChunkCoordinates spawnCoords) {
		playerCoordsMap.put(entityID, spawnCoords);
	}

	/**
	 * Gets the spawn coords of a player from the map
	 * @param player Player to get the spawn coords of
	 * @return Spawn coords of player
	 */
	public static ChunkCoordinates loadCoords(EntityPlayer player) {
		return playerCoordsMap.get(player.entityId);
	}

	/**
	 * Called right after a player is done sleeping, restore spawn coords
	 */
	public void onPostSleepTick(Object obj) {
		if (obj instanceof WorldServer) {
			WorldServer world = (WorldServer)obj;

			// If all the players are at the last stage of sleep
			if (areAllPlayersAsleep) {
				// Iterate through the players in guest beds
				for (Integer id : playerCoordsMap.keySet()) {
					Entity e = world.getEntityByID(id);

					// Restore spawn data
					if (e instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer)e;
						player.setSpawnChunk(loadCoords(player), false);
						playerCoordsMap.remove(player.entityId);
						//	player.addChatMessage("Restoring spawn data for player " + player.username);
					}
				}
			}
		}
	}
}