package nomble.MSPal.Section.Impl;

import com.vdurmont.emoji.EmojiManager;

import java.util.List;
import java.util.Random;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Section.ISection;

import sx.blah.discord.api.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.util.*;

public class SectionLog implements ISection{
	private IDiscordClient bot;
	private Bot main;

	public SectionLog(IDiscordClient b, Bot m){
		load();

		bot = b;
		main = m;
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent e){
		List<String[]> sl = Util.getCommand(e.getMessage().getContent());
		long l = e.getMessage().getGuild().getLongID();

		for(String[] sa : sl){
			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");
			if(c.equals("logbet") && sa.length >= 3){
				//new Thread(() -> {
					IChannel ic = e.getMessage().getChannel();
					try{
						String s = "\n-------END " + ic.getName().toUpperCase() + " LOG-------";

						MessageHistory mh = ic.getMessageHistoryIn(Long.valueOf(sa[1]), Long.valueOf(sa[2]), 500000);
						for(IMessage m : mh){
							s = "\n" + m.getAuthor().getName() + ": " + m.getContent() + s;
						}
						s += "-------BEGIN LOG OF " + ic.getName().toUpperCase() + " (" + ic.getLongID() + "): " + sa[1] + " TO " + sa[2] + "-------";

						String p = main.upload(ic.getName() + " Log", s);

						RequestBuffer.request(() -> {
							ic.sendMessage(p);
						});
					}
					catch(NumberFormatException nfe){
						RequestBuffer.request(() -> {
							ic.sendMessage("Invalid message ids: must be numbers.");
						});
					}
				//}).start();
			}
		}
	}

	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"logbet", "Log between two messages. Takes two message ids as arguments. Limited to 500000 messages."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"scroll", "Log", "Document channel histories.", ":scroll:"};
	}

	@Override
	public void load(){}
}
