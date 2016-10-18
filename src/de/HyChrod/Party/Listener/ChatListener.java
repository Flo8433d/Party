/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Listener;

import de.HyChrod.Party.FileManager;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.Uti.PartyManager;
import de.HyChrod.Party.Uti.PlayerUtilitites;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {

	private Party plugin;

	public ChatListener(Party plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(ChatEvent e) {

		if (e.getSender() instanceof ProxiedPlayer) {

			ProxiedPlayer p = ((ProxiedPlayer) e.getSender());

			PlayerUtilitites pu = new PlayerUtilitites(p);

			if (FileManager.ConfigCfg.getBoolean("Party.Options.PartyChat.Enable")) {

				String code = FileManager.ConfigCfg.getString("Party.Options.PartyChat.Code");
				if (e.getMessage().startsWith(code)) {
					e.setCancelled(true);
					
					if (pu.getParty() != null) {
						
						PartyManager mgr = pu.getParty();
						for (ProxiedPlayer members : mgr.getMembers()) {
							members.sendMessage(
									plugin.getString("Messages.PartyChatFormat").replace("%PLAYER%", p.getName())
											.replace("%MESSAGE%", e.getMessage().replace(code, "")));
						}
						mgr.getLeader().sendMessage(
								plugin.getString("Messages.PartyChatFormat").replace("%PLAYER%", p.getName())
										.replace("%MESSAGE%", e.getMessage().replace(code, "")));
						return;

					} else {
						p.sendMessage(plugin.getString("Messages.PartyChat.NoParty"));
						return;
					}

				}
			}
		}
	}

}
