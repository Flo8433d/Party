/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Uti;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReflectionsManager {
	
	public static void sendHoverMessage(ProxiedPlayer player, ProxiedPlayer adder, String message, String[] msgs,
			String[] hover, String[] command) {
		try {
			TextComponent component = new TextComponent(message.replace("%ACCEPT_BUTTON%", "").replace("%DENY_BUTTON%", ""));
			for (int i = 0; i < msgs.length; i++) {
				TextComponent com1 = new TextComponent(msgs[i] + "   ");
				com1.setClickEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, command[i].replace("%NAME%", adder.getName())));
				com1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover[i])));
				component.addExtra(com1);
			}
			player.sendMessage(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
