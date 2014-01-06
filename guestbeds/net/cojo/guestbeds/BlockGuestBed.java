package guestbeds.net.cojo.guestbeds;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockGuestBed extends BlockBed {

	@SideOnly(Side.CLIENT)
	private Icon[] bedEndIcons;
	@SideOnly(Side.CLIENT)
	private Icon[] bedSideIcons;
	@SideOnly(Side.CLIENT)
	private Icon[] bedTopIcons;

	public BlockGuestBed(int id) {
		super(id);
	}

	/**
	 * Determines if this block is classified as a Bed, Allowing
	 * players to sleep in it, though the block has to specifically
	 * perform the sleeping functionality in it's activated event.
	 *
	 * @param world The current world
	 * @param x X Position
	 * @param y Y Position
	 * @param z Z Position
	 * @param player The player or camera entity, null in some cases.
	 * @return True to treat this as a bed
	 */
	@Override
	public boolean isBed(World world, int x, int y, int z, EntityLivingBase player) {
		return true;
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9) {
		// When the block is first activated, save off the coordinates
		if (!par1World.isRemote) {
			TickHandlerSleep.saveCoords(player.entityId, player.getBedLocation(player.dimension));
			//player.addChatMessage("Saving spawn data for player " + player.username + " " + player.entityId);
		}
		
		return super.onBlockActivated(par1World, par2, par3, par4, player, par6, par7, par8, par9);
	}

	@SideOnly(Side.CLIENT)

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	public Icon getIcon(int par1, int par2) {
		if (par1 == 0) {
			return Block.planks.getBlockTextureFromSide(par1);
		}
		else {
			int k = getDirection(par2);
			int l = Direction.bedDirection[k][par1];
			int i1 = isBlockHeadOfBed(par2) ? 1 : 0;
			return (i1 != 1 || l != 2) && (i1 != 0 || l != 3) ? (l != 5 && l != 4 ? this.bedTopIcons[i1] : this.bedSideIcons[i1]) : this.bedEndIcons[i1];
		}
	}

	@SideOnly(Side.CLIENT)

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	public void registerIcons(IconRegister par1IconRegister) {
		this.bedTopIcons = new Icon[] {par1IconRegister.registerIcon(GuestBedsMod.MODID + ":" + this.getTextureName() + "_feet_top"), par1IconRegister.registerIcon(GuestBedsMod.MODID + ":" + this.getTextureName() + "_head_top")};
		this.bedEndIcons = new Icon[] {par1IconRegister.registerIcon(GuestBedsMod.MODID + ":" + this.getTextureName() + "_feet_end"), par1IconRegister.registerIcon(GuestBedsMod.MODID + ":" + this.getTextureName() + "_head_end")};
		this.bedSideIcons = new Icon[] {par1IconRegister.registerIcon(GuestBedsMod.MODID + ":" + this.getTextureName() + "_feet_side"), par1IconRegister.registerIcon(GuestBedsMod.MODID + ":" + this.getTextureName() + "_head_side")};
	}

}
