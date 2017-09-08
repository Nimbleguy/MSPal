package nomble.MSPal.Commands.Impl;

import com.vdurmont.emoji.EmojiManager;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Commands.ISection;

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
				new Thread(() -> {
					IChannel ic = e.getMessage().getChannel();
					try{
						MessageHistory mh = ic.getMessageHistoryIn(Long.valueOf(sa[2]), Long.valueOf(sa[1]), 5000);
						String s = log(mh, "CHANNEL " + ic.getName(), ic.getLongID(), sa[1] + " TO " + sa[2]);

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
				}).start();
			}
			else if(c.equals("lognote") && sa.length >= 3){
				switch(sa[1]){
					case "create":
						
						break;
					case "get":
						break;
					default:
						break;
				}

				//save ids
			}
		}
	}

	@Override
	public String[][] getInfo(long l){
		return new String[][] {{"logbet", "Log between two messages. Takes two message ids as arguments. Limited to 5000 messages in one go."},
			{"lognote", "Take notes down to recall later. Takes two arguments: a note id and a message. If you don't have a note id, you will be given one by using " +
				"`create` instead of the id. As an example: `" + Util.getPrefix(l) + "lognote" + Util.getSuffix(l) + " create this is my message` " +
				"to get a note id for a note with the text `this is my message`. To add more to the note, you may do this: " +
				"`" + Util.getPrefix(l) + "lognote" + Util.getSuffix(l) + " [your note id] this will be added to my message`. " +
				"to get the note contents, just execute `" + Util.getPrefix(l) + "lognote" + Util.getSuffix(l) + " get [your note id]`. " +
				"Notes are limited to 2500 messages and expire after a week."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"scroll", "Log", "Document channel histories. To get message ids, open discord settings, go to appearance, and enable developer mode. " +
			"You will now be able to copy message ids in the same interface as the options to pin and delete messages.", ":scroll:",
			"By using these commands in a channel, you expressly agree to the collection of channel ids and names for publication in the logs." +
			"To be logged, you must explicitly opt-in to information collection. The command for that is located in the Bot Commands section."};
	}

	@Override
	public void load(){}

	private String log(MessageHistory mh, String n, long i, String o){
		String s = "\n-------END " + n.toUpperCase() + " LOG-------";

		for(IMessage m : mh){
			if(Util.canConsent(m.getAuthor().getLongID()){
				String ts = m.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
				s = "\n" + m.getAuthor().getName() + " (" + ts + " UTC): " + m.getContent() + s;
			}
		}

		s = "-------BEGIN LOG OF " + n.toUpperCase() + " (" + i + "): " + o + "-------" + s;

		return s;
	}
}
