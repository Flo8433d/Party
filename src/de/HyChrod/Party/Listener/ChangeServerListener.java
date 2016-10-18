/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Listener;

import java.util.Random;

import de.HyChrod.Party.FileManager;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.Uti.PartyManager;
import de.HyChrod.Party.Uti.PlayerUtilitites;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@SuppressWarnings("deprecation")
public class ChangeServerListener implements Listener {

	private Party plugin;
	
	public ChangeServerListener(Party plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerChange(ServerSwitchEvent e) {
		ProxiedPlayer p = e.getPlayer();
		PlayerUtilitites pu = new PlayerUtilitites(p);
		
		if(pu.getParty() == null) return;
		PartyManager mgr = pu.getParty();
		if(mgr.isLeader(p)) {
			mgr.partyOnMove = true;
			
			mgr.getLeader().sendMessage(plugin.getString("Messages.PartyChangeServer").replace("%SERVER%", e.getPlayer().getServer().getInfo().getName()));
			for(ProxiedPlayer member : mgr.getMembers()) {
				member.sendMessage(plugin.getString("Messages.PartyChangeServer").replace("%SERVER%", e.getPlayer().getServer().getInfo().getName()));
				member.connect(p.getServer().getInfo());
			}
			return;
		}
		if(mgr.partyOnMove == true) return;
		pu.removeParty();
		mgr.removeMember(p);
		mgr.getLeader().sendMessage(plugin.getString("Messages.Commands.Leave.LeaveMessage").replace("%PLAYER%", p.getName()));
		for(ProxiedPlayer members : mgr.getMembers()) {
			members.sendMessage(plugin.getString("Messages.Commands.Leave.LeaveMessage").replace("%PLAYER%", p.getName()));
		}
		if(mgr.getLeader().equals(p)) {
			if(FileManager.ConfigCfg.getBoolean("Party.Options.DeleteOnQuit")) {
				for(ProxiedPlayer members : mgr.getMembers()) {
					PlayerUtilitites mu = new PlayerUtilitites(members);
					mu.removeParty();
					members.sendMessage(plugin.getString("Messages.Commands.Leave.Dissolve"));
				}
			} else {
				ProxiedPlayer newLeader = mgr.getMembers().get(new Random().nextInt(mgr.getMembers().size()));
				mgr.setLeader(newLeader);
				newLeader.sendMessage(plugin.getString("Messages.Commands.Leave.NewLeader").replace("%PLAYER%", newLeader.getName()));
				for(ProxiedPlayer members : mgr.getMembers()) {
					members.sendMessage(plugin.getString("Messages.Commands.Leave.NewLeader").replace("%PLAYER%", newLeader.getName()));
				}
			}
		}
	}
	
}
