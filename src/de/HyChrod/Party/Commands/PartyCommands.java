/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Commands;

import java.util.LinkedList;
import java.util.Random;

import de.HyChrod.Party.FileManager;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.SQL.SQL_Manager;
import de.HyChrod.Party.Uti.PartyManager;
import de.HyChrod.Party.Uti.PlayerUtilitites;
import de.HyChrod.Party.Uti.ReflectionsManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommands extends Command {

	private Party plugin;
	public static LinkedList<ProxiedPlayer> disabled = new LinkedList<>();
	
	public PartyCommands(String name, Party plugin) {
		super(name);
		this.plugin = plugin; 
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			
			if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help")) || (args.length == 2 && args[0].equalsIgnoreCase("help") 
					&& args[1].equals("1"))) {
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageI"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageII"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageIII"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageIV"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageV"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageVI"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageVII"));
				p.sendMessage(plugin.getString("Messages.Commands.Help.MessageVIII"));
				return;
			}
			
			if(args[0].equalsIgnoreCase("invite")) {
				if(args.length != 2) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/party invite <Player>"));
					return;
				}
				
				PlayerUtilitites pu = new PlayerUtilitites(p);
				PartyManager mgr = new PartyManager(p);
				if(pu.isInParty()) {
					mgr = pu.getParty();
					if(!mgr.isLeader(p)) {
						p.sendMessage(plugin.getString("Messages.Commands.Invite.AlreadyInParty"));
						return;
					}
				}
				
				if(BungeeCord.getInstance().getPlayer(args[1]) == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Invite.PlayerOffline"));
					return;
				}
				
				ProxiedPlayer b = BungeeCord.getInstance().getPlayer(args[1]);
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
					if(SQL_Manager.get(b.getUniqueId().toString(), "OPTIONS").contains("option_noParty") || disabled.contains(b)) {
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
			
			if(args[0].equalsIgnoreCase("accept")) {
				if(args.length != 2) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/party accept <Player>"));
					return;
				}
				PlayerUtilitites pu = new PlayerUtilitites(p);
				if(pu.isInParty()) {
					p.sendMessage(plugin.getString("Messages.Commands.Accept.AlreadyInParty"));
					return;
				}
				if(BungeeCord.getInstance().getPlayer(args[1]) == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Accept.PlayerOffline"));
					return;
				}
				ProxiedPlayer b = BungeeCord.getInstance().getPlayer(args[1]);
				PlayerUtilitites bu = new PlayerUtilitites(b);
				if(!pu.getInvites().contains(b)) {
					p.sendMessage(plugin.getString("Messages.Commands.Accept.NoInvite"));
					return;
				}
				PartyManager mgr = new PartyManager(b);
				if(bu.isInParty())
					mgr = bu.getParty();
				else
					bu.setParty(mgr);
				if(mgr == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Accept.NoMoreAvailable"));
					return;
				}
				if((mgr.getMembers().size()+1) 
						>= FileManager.ConfigCfg.getInt("Party.Options.PlayerLimit")) {
					if(!mgr.getLeader().hasPermission("Party.ExtendedFriends") 
							|| ((mgr.getMembers().size()+1) >= FileManager.ConfigCfg.getInt("Party.Options.ExtendedPlayerLimit"))) {
						p.sendMessage(plugin.getString("Messages.Commands.Accept.ToManyPlayers"));
						return;
					}
				}
				
				mgr.addMember(p);
				pu.setParty(mgr);
				pu.removeInvite(b);
				
				p.sendMessage(plugin.getString("Messages.Commands.Accept.Accept").replace("%PLAYER%", b.getName()));
				b.sendMessage(plugin.getString("Messages.Commands.Accept.ToAccept").replace("%PLAYER%", p.getName()));
				mgr.getLeader().sendMessage(plugin.getString("Messages.Commands.Accept.JoinParty").replace("%PLAYER%", p.getName()));
				for(ProxiedPlayer members : mgr.getMembers()) {
					members.sendMessage(plugin.getString("Messages.Commands.Accept.JoinParty").replace("%PLAYER%", p.getName()));
				}
				return;
				
			}
			
			if(args[0].equalsIgnoreCase("deny")) {
				if(args.length != 2) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/party deny <Player>"));
					return;
				}
				if(BungeeCord.getInstance().getPlayer(args[1]) == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Deny.PlayerOffline"));
					return;
				}
				PlayerUtilitites pu = new PlayerUtilitites(p);
				ProxiedPlayer b = BungeeCord.getInstance().getPlayer(args[1]);
				if(!pu.getInvites().contains(b)) {
					p.sendMessage(plugin.getString("Messages.Commands.Deny.NoInvite"));
					return;
				}
				pu.removeInvite(b);
				b.sendMessage(plugin.getString("Messages.Commands.Deny.ToDeny").replace("%PLAYER%", p.getName()));
				p.sendMessage(plugin.getString("Messages.Commands.Deny.Deny").replace("%PLAYER%", b.getName()));
				return;
			}
			
			if(args[0].equalsIgnoreCase("leave")) {
				if(args.length != 1) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/party leave"));
					return;
				}
				PlayerUtilitites pu = new PlayerUtilitites(p);
				if(pu.getParty() == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Leave.NoParty"));
					return;
				}
				PartyManager mgr = pu.getParty();
				p.sendMessage(plugin.getString("Messages.Commands.Leave.Leave"));
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
				return;
			}
			
			if(args[0].equalsIgnoreCase("kick")) {
				if(args.length != 2) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/party kick <Player>"));
					return;
				}
				PlayerUtilitites pu = new PlayerUtilitites(p);
				if(pu.getParty() == null) {
					p.sendMessage(plugin.getString("Messages.Commands.Kick.NoParty"));
					return;
				}
				PartyManager mgr = pu.getParty();
				if(mgr.getLeader().equals(p)) {
					if(mgr.getMembers().contains(BungeeCord.getInstance().getPlayer(args[1]))) {
						
						ProxiedPlayer b = BungeeCord.getInstance().getPlayer(args[1]);
						mgr.removeMember(b);
						for(ProxiedPlayer members : mgr.getMembers()) {
							members.sendMessage(plugin.getString("Messages.Commands.Kick.Broadcast").replace("%PLAYER%", b.getName()));
						}
						p.sendMessage(plugin.getString("Messages.Commands.Kick.Kick").replace("%PLAYER%", b.getName()));
						b.sendMessage(plugin.getString("Messages.Commands.Kick.ToKick"));
						return;
						
					}
					p.sendMessage(plugin.getString("Messages.Commands.Kick.NotInParty"));
					return;
				}
				p.sendMessage(plugin.getString("Messages.Commands.Kick.NoLeader"));
				return;
			}
			
			if(args[0].equalsIgnoreCase("toggle")) {
				if(args.length != 1) {
					p.sendMessage(plugin.getString("Messages.Commands.WrongUsage").replace("%COMMAND%", "/party toggle"));
					return;
				}
				if(Party.friends) {
					LinkedList<String> hash = SQL_Manager.get(p.getUniqueId().toString(), "OPTIONS");
					if(hash.contains("option_noParty"))
						hash.remove("option_noParty");
					else
						hash.add("option_noParty");
					SQL_Manager.set(hash, p.getUniqueId().toString(), "OPTIONS");
				} else {
					if(disabled.contains(p))
						disabled.remove(p);
					else
						disabled.add(p);
				}
				p.sendMessage(plugin.getString("Messages.Commands.Toggle"));
				return;
				
			}
			
		} else {
			sender.sendMessage(plugin.getString("Messages.Commands.NoPlayer"));
			return;
		}
	}

}
