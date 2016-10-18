/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Uti;

import java.util.LinkedList;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyManager {

	private static LinkedList<PartyManager> partys = new LinkedList<>();
	
	private ProxiedPlayer leader;
	public boolean partyOnMove = false;
	private LinkedList<ProxiedPlayer> members = new LinkedList<>();
	
	public PartyManager(ProxiedPlayer leader) {
		this.leader = leader;
	}
	
	public boolean isLeader(ProxiedPlayer player) {
		return leader.equals(player);
	}
	
	public boolean isInParty(ProxiedPlayer player) {
		if(members.contains(player) || leader.equals(player))
			return true;
		return false;
	}
	
	public ProxiedPlayer getLeader() {
		return leader;
	}
	
	public LinkedList<ProxiedPlayer> getMembers() {
		return members;
	}
	
	public void setLeader(ProxiedPlayer newLeader) {
		if(members.contains(newLeader))
			members.remove(newLeader);
		leader = newLeader;
	}
	
	public void removeMember(ProxiedPlayer player) {
		if(members.contains(player))
			members.remove(player);
	}
	
	public void addMember(ProxiedPlayer player) {
		if(!members.contains(player))
			members.add(player);
	}
	
	public static LinkedList<PartyManager> getPartys() {
		return partys;
	}
	
}
