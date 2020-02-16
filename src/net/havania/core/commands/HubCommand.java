package net.havania.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.havania.core.Core;
import net.havania.core.utils.Server;
import net.havania.core.utils.Server.ServerStatus;
import net.havania.core.utils.Server.ServerType;

public class HubCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			
			for(Server server : Core.getCore().getServersByType(ServerType.LOBBY))
			{
				if(server.getStatus().equals(ServerStatus.READY))
				{
					if(server.getMaxPlayer() == -1 || server.getCurrentPlayer() < server.getMaxPlayer())
					{
						p.sendMessage("§7Teleportation...");
						server.connectPlayer(p);
						return true;
					}
				}
			}
			
			p.sendMessage("§cAucun lobby n'est disponible !");
		}
		
		return false;
	}
}
