package soarvivor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.lib.LogHelper;
import soarvivor.lib.ModInfo;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Handler for custom communication packets for Soarvivor mod
 */
public class PacketHandler implements IPacketHandler
{
	/**
	 * Defines a packet id subtype to allow multiple packet types on a single
	 * channel
	 */
	public static final byte	EXTENDED_PROPERTIES	= 1, OPEN_SERVER_GUI = 2;

	// Don't need to do anything here.
	public PacketHandler()
	{}

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		// Get the data stream
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte packetType;

		// Read our custom packet header to determine what kind of packet this
		// is.
		try
		{
			packetType = inputStream.readByte();
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		// Now we check which channel, in case you have more than one registered
		if (packet.channel.equals(ModInfo.CHANNEL))
		{
			// Handle each case appropriately:
			switch (packetType)
			{
				case EXTENDED_PROPERTIES:
					handleExtendedProperties(packet, player, inputStream);
					break;
				case OPEN_SERVER_GUI:
					handleOpenGuiPacket(packet, (EntityPlayer)player, inputStream);
					break;
				default:
					LogHelper.log(Level.WARNING, "[PACKET HANDLER] Unknown packet type "
							+ packetType);
			}
		}
	}

	// Making different methods to handle each channel helps keep things tidy:

	/**
	 * Handle an extended player properties packet
	 * 
	 * @param packet
	 * @param player
	 * @param inputStream
	 */
	private void handleExtendedProperties(Packet250CustomPayload packet, Player player,
			DataInputStream inputStream)
	{
		// DataInputStream inputStream = new DataInputStream(new
		// ByteArrayInputStream(packet.data));
		ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);

		// Everything we read here should match EXACTLY the order in which we
		// wrote it to the output stream in our ExtendedPlayer sync() method.
		try
		{
			props.setCurrentHydration(inputStream.readInt());
			props.setCurrentIce(inputStream.readInt());
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
	}

	/**
	 * This method will open the appropriate server gui element for the player
	 * 
	 * @param packet
	 * @param player
	 * @param inputStream
	 */
	private void handleOpenGuiPacket(Packet250CustomPayload packet, EntityPlayer player,
			DataInputStream inputStream)
	{
		int guiID;
		// inputStream is already open, so we don't need to do anything other
		// than continue reading from it:
		try
		{
			guiID = inputStream.readInt();
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		// Now we can open the server gui element, which will automatically open
		// the client element as well
		player.openGui(soarvivor.soarvivor.instance, guiID, player.worldObj, (int)player.posX,
				(int)player.posY, (int)player.posZ);
	}

	// Here's the method we used in the last step to send the packet:
	/**
	 * Sends a packet to the server telling it to open gui for player
	 * 
	 * @param guiId
	 */
	public static final void sendOpenGuiPacket(int guiId)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(bos);

		try
		{
			outputStream.writeByte(OPEN_SERVER_GUI);
			outputStream.writeInt(guiId);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(ModInfo.CHANNEL,
				bos.toByteArray()));
	}
}
