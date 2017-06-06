package com.tisawesomeness.minecord.command.player;

import java.awt.Color;
import java.util.Arrays;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UuidCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"uuid",
			"Gets the UUID of a player.",
			"<username> [date]",
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Config.getPrefix() + "uuid <username> [date]" +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}
		
		String username = args[0];
		String uuid = null;
		
		//Parse date argument
		if (args.length > 1) {
			long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
			if (timestamp == -1) {
				String m = ":x: Improperly formatted date. " +
					"At least a date or time is required. " +
					"Do `" + Config.getPrefix() + "uuid` for more info.";
				return new Result(Outcome.WARNING, m);
			}
			
		//Get the UUID
			uuid = NameUtils.getUUID(username, timestamp);
		} else {
			uuid = NameUtils.getUUID(username);
		}
		
		//Check for errors
		if (uuid == null) {
			String m = ":x: The Mojang API could not be reached." +
				"\n" + "Are you sure that username exists?";
			return new Result(Outcome.WARNING, m, 1.5);
		} else if (!uuid.matches(NameUtils.uuidRegex)) {
			String m = ":x: The API responded with an error:\n" + uuid;
			return new Result(Outcome.ERROR, m, 3);
		}
		
		//Get NameMC url
		String url = "https://namemc.com/profile/" + uuid;
		
		//PROPER APOSTROPHE GRAMMAR THANK THE LORD
		String title = username;
		if (title.endsWith("s")) {
			title = title + "' UUID";
		} else {
			title = title + "'s UUID";
		}
		
		MessageEmbed me = MessageUtils.embedMessage(title, url, uuid, Color.GREEN);
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
	}
	
}
