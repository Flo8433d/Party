/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party;

import java.io.IOException;

import de.HyChrod.Party.Commands.PartyCommands;
import de.HyChrod.Party.Listener.ChangeServerListener;
import de.HyChrod.Party.Listener.ChannelListener;
import de.HyChrod.Party.Listener.ChatListener;
import de.HyChrod.Party.Listener.QuitListener;
import de.HyChrod.Party.SQL.MySQL;
import de.HyChrod.Party.Uti.AsyncMySQLReconnecter;
import de.HyChrod.Party.Uti.Metrics;
import de.HyChrod.Party.Uti.UpdateChecker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

public class Party extends Plugin {

	public String prefix;
	private static Party instance = null;
	public static Boolean friends = false;

	private FileManager mgr = new FileManager();

	@Override
	public void onEnable() {
		this.mgr.createFolders(this);
		if ((FileManager.ConfigCfg == null) || (FileManager.ConfigCfg.getString("Party.Prefix") == null)) {
			BungeeCord.getInstance().stop();
			return;
		}
		this.prefix = ChatColor.translateAlternateColorCodes('&', FileManager.ConfigCfg.getString("Party.Prefix"));
		instance = this;

		if (FileManager.ConfigCfg.getBoolean("Party.Friends2_0.Enable")) {
			mgr.readMySQLData();
			MySQL.connect();
			friends = true;

			if (!MySQL.isConnected()) {
				System.out.println("");
				System.out.println("");
				System.out.println("Party | Can't connect to mysql!");
				System.out.println("Party | Please check your login data and try again!");
				System.out.println("");
				System.out.println("");
				return;
			}
			new AsyncMySQLReconnecter();
		}

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}

		registerClasses();
		System.out.println("Party | ---------------------------------------------");
		if (FileManager.ConfigCfg.getBoolean("Party.CheckForUpdate") && !UpdateChecker.check()) {
			System.out.println("Party | A new update of this plugin is available");
			System.out.println("Party | Please update to the newest version!");
			System.out.println("Party | You will get no support for this version!");
		}
		System.out.println("Party | ");
		System.out.println("Party | The plugin was loaded successfully!");
		System.out.println("Party | ");
		System.out.println("Party | ---------------------------------------------");

		BungeeCord.getInstance().getPluginManager().registerListener(this, new ChannelListener(this));
		BungeeCord.getInstance().registerChannel("Return");
	}

	private void registerClasses() {
		BungeeCord.getInstance().getPluginManager().registerCommand(this, new PartyCommands("Party", this));
		BungeeCord.getInstance().getPluginManager().registerListener(this, new QuitListener(this));
		BungeeCord.getInstance().getPluginManager().registerListener(this, new ChangeServerListener(this));
		BungeeCord.getInstance().getPluginManager().registerListener(this, new ChatListener(this));
	}

	@Override
	public void onDisable() {

	}

	public String getString(String path) {
		return ChatColor.translateAlternateColorCodes('&',
				FileManager.MessagesCfg.getString(path).replace("%PREFIX%", this.prefix));
	}

	public static Party getInstance() {
		return instance;
	}

}
