/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Uti;

import java.util.HashMap;
import java.util.LinkedList;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtilitites {

	private ProxiedPlayer player;
	private static HashMap<ProxiedPlayer, PartyManager> partys = new HashMap<>();
	private static HashMap<ProxiedPlayer, LinkedList<ProxiedPlayer>> invites = new HashMap<>();
	
	public PlayerUtilitites(ProxiedPlayer player) {
		this.player = player;
	}
	
	public ProxiedPlayer getPlayer() {
		return player;
	}
	
	public boolean isInParty() {
		return partys.containsKey(this.player);
	}
	
	public PartyManager getParty() {
		return isInParty() ? partys.get(this.player) : null;
	}
	
	public void setParty(PartyManager mgr) {
		if(partys.containsKey(this.player)) return;
		partys.put(this.player, mgr);
	}
	
	public void addInvite(ProxiedPlayer player) {
		LinkedList<ProxiedPlayer> hash = new LinkedList<>();
		if(invites.containsKey(this.player)) hash = invites.get(this.player);
		if(!hash.contains(player)) hash.add(player);
		invites.put(this.player, hash);
	}
	
	public void removeInvite(ProxiedPlayer player) {
		LinkedList<ProxiedPlayer> hash = new LinkedList<>();
		if(invites.containsKey(this.player)) hash = invites.get(this.player);
		if(hash.contains(player)) hash.remove(player);
		invites.put(this.player, hash);
	}
	
	public LinkedList<ProxiedPlayer> getInvites() {
		return invites.containsKey(this.player) ? invites.get(this.player) : new LinkedList<ProxiedPlayer>();
	}
	
	public void removeParty() {
		if(partys.containsKey(this.player)) partys.remove(this.player);
	}
	
}
