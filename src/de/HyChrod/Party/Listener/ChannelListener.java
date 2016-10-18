/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Party.FileManager;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.Commands.PartyCommands;
import de.HyChrod.Party.SQL.SQL_Manager;
import de.HyChrod.Party.Uti.PartyManager;
import de.HyChrod.Party.Uti.PlayerUtilitites;
import de.HyChrod.Party.Uti.ReflectionsManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@SuppressWarnings("deprecation")
public class ChannelListener implements Listener {

	private Party plugin;
	
	public ChannelListener(Party plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlugin(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("BungeeCord")) {
			
			ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
			String subchannel = in.readUTF();
			
			if (subchannel.startsWith("InvitePlayer")) {
				try {
					String[] splitted1 = subchannel.split("::");
					String[] players = splitted1[1].split("@");
					
					if (BungeeCord.getInstance().getPlayer(players[0]) != null) {
						ProxiedPlayer p = BungeeCord.getInstance().getPlayer(players[1]);
						
						PlayerUtilitites pu = new PlayerUtilitites(p);
						PartyManager mgr = new PartyManager(p);
						if(pu.isInParty()) {
							mgr = pu.getParty();
							if(!mgr.isLeader(p)) {
								p.sendMessage(plugin.getString("Messages.Commands.Invite.AlreadyInParty"));
								return;
							}
						}
						
						if(BungeeCord.getInstance().getPlayer(players[0]) == null) {
							p.sendMessage(plugin.getString("Messages.Commands.Invite.PlayerOffline"));
							return;
						}
						
						ProxiedPlayer b = BungeeCord.getInstance().getPlayer(players[0]);
						PlayerUtilitites bu = new PlayerUtilitites(b);
						if(bu.isInParty()) {
							p.sendMessage(plugin.getString("Messages.Commands.Invite.PlayerInParty"));
							return;
						}
						
						if((mgr.getMembers().size()+1) >= FileManager.ConfigCfg.getInt("Party.Options.PlayerLimit")) {
							if(!p.hasPermission("Party.ExtendedFriends") 
									|| ((mgr.getMembers().size()+1) >= FileManager.ConfigCfg.getInt("Party.Options.ExtendedPlayerLimit"))) {
								p.sendMessage(plugin.getString("Messages.Commands.Invite.ToManyPlayers"));
								return;
							}
						}
						
						if(Party.friends) {
							if(FileManager.ConfigCfg.getBoolean("Party.Friends2_0.OnlyFriends")) {
								if(!SQL_Manager.get(p.getUniqueId().toString(), "FRIENDS").contains(b.getUniqueId().toString())) {
									p.sendMessage(plugin.getString("Messages.Commands.Invite.Friends.NoFriends"));
									return;
								}
							}
							if(SQL_Manager.get(b.getUniqueId().toString(), "OPTIONS").contains("option_noParty") || PartyCommands.disabled.contains(b)) {
								p.sendMessage(plugin.getString("Messages.Commands.Invite.Friends.NoInvite"));
								return;
							}
						}
						
						if(bu.getInvites().contains(p)) {
							p.sendMessage(plugin.getString("Messages.Commands.Invite.AlreadyInvited"));
							return;
						}
						
						b.sendMessage(plugin.getString("Messages.Commands.Invite.ToInvite.MessageI").replace("%PLAYER%", p.getName()));
						b.sendMessage(plugin.getString("Messages.Commands.Invite.ToInvite.MessageII").replace("%PLAYER%", p.getName()));

						String[] msgs = new String[2];
						msgs[0] = plugin.getString("Messages.Commands.Invite.AcceptButton");
						msgs[1] = plugin.getString("Messages.Commands.Invite.DenyButton");

						String[] hover = new String[2];
						hover[0] = plugin.getString("Messages.Commands.Invite.AcceptHover");
						hover[1] = plugin.getString("Messages.Commands.Invite.DenyHover");

						String[] command = new String[2];
						command[0] = "/party accept %NAME%";
						command[1] = "/party deny %NAME%";
						
						ReflectionsManager.sendHoverMessage(b, p, plugin.getString("Messages.Commands.Invite.ToInvite.MessageIII"), msgs, hover, command);
						bu.addInvite(p);
						b.sendMessage(plugin.getString("Messages.Commands.Invite.ToInvite.MessageIV"));
						
						p.sendMessage(plugin.getString("Messages.Commands.Invite.Invite").replace("%PLAYER%", b.getName()));
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return;
			}

		}
	}

}
