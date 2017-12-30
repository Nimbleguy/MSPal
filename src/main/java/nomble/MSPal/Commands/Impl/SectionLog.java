package nomble.MSPal.Commands.Impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Commands.EnumSection;
import nomble.MSPal.Commands.ISection;
import nomble.MSPal.Data.Impl.DataLog;
import nomble.MSPal.Data.Impl.DataLog.EnumResults;
import nomble.MSPal.Data.Impl.DataConsent;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.util.*;

public class SectionLog implements ISection{
	private Bot main;

	public SectionLog(Bot m){
		load();

		main = m;
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent e){
		long l = -1;
		if(!(e.getChannel() instanceof IPrivateChannel)){
			l = e.getMessage().getGuild().getLongID();
		}

		List<String[]> sl = Util.getCommand(e.getMessage().getContent(), l);
		IChannel ic = e.getChannel();

		for(String[] sa : sl){
			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");
			if(c.equals("logbet") && sa.length >= 3){
				new Thread(() -> {
					try{
						MessageHistory mh = ic.getMessageHistoryIn(Long.valueOf(sa[2]), Long.valueOf(sa[1]), 5000);
						String s = log(mh, "CHANNEL " + ic.getName(), ic.getLongID(), sa[1] + " TO " + sa[2], e.getAuthor().getPermissionsForGuild(e.getMessage().getGuild()).contains(Permissions.MANAGE_MESSAGES));

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
				DataLog dl = main.getData(DataLog.class);
				long li;
				int n;
				switch(sa[1]){
					case "create":
						Random r = new Random();
						while(dl.getFreeIndex(li = r.nextLong()) != 0);
						
						dl.addNote(li, e.getMessage());

						final long lif = li;
						RequestBuffer.request(() -> {
							e.getAuthor().getOrCreatePMChannel().sendMessage("Your note id is " + lif + ".");
						});
						break;
					case "get":
						li = Long.valueOf(sa[2]);
						n = dl.getFreeIndex(li);
						StringBuilder sb = new StringBuilder();
						
						for(short i = 0; i < n; i++){
							String[] s = dl.getLog(li, i);
							String u = s[EnumResults.USR.ordinal()];
							String t = s[EnumResults.TIME.ordinal()];
							String m = s[EnumResults.MSG.ordinal()];
							sb.append(u + " (" + t + " UTC): " + m + "\n");
						}
						
						String p = main.upload(e.getAuthor().getName() + " Notes", sb.toString());

						RequestBuffer.request(() -> {
							ic.sendMessage(p);
						});
						break;
					default:
						li = Long.valueOf(sa[1]);
						
						if(!dl.addNote(li, e.getMessage())){
							RequestBuffer.request(() -> {
								ic.sendMessage("There are too many messages in that log.");
							});
						}
						break;
				}
			}
		}
	}

	@Override
	public String[][] getInfo(long l){
		return new String[][] {{"logbet", "Log between two messages. Takes two message ids as arguments. Limited to 5000 messages in one go."},
			{"lognote", "Take notes down to recall later. Takes two arguments: a note id and a message. If you don't have a note id, you will be given one by using " +
				"`create` instead of the id. As an example: `" + Util.getPrefix(l) + "lognote" + Util.getSuffix(l) + " create this is the first message` " +
				"to get a note id for a note with the text `this is my message`. To add more to the note, you may do this: " +
				"`" + Util.getPrefix(l) + "lognote" + Util.getSuffix(l) + " [your note id] this will be added to my message`. " +
				"to get the note contents, just execute `" + Util.getPrefix(l) + "lognote" + Util.getSuffix(l) + " get [your note id]`. " +
				"Notes are limited to 256 messages and expire after a week."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"scroll", EnumSection.LOG.toString(), "Document channel histories.", ":scroll:",
			"By using these commands in a channel, you expressly agree to the collection of channel ids and names for publication in the logs." +
			"To be logged, you must explicitly opt-in to information collection (stores username, user ids, messages, and timestamp depending on the command) or the logger must have the \"Manage Messages\" permission. The command for that is located in the Bot Commands section."};
	}

	@Override
	public void load(){}

	private String log(MessageHistory mh, String n, long i, String o, boolean b){
		StringBuilder sb = new StringBuilder();
		sb.append("\n------- END " + n.toUpperCase() + " LOG -------");

		for(IMessage m : mh){
			if(main.getData(DataConsent.class).getConsent(m.getAuthor().getLongID()) || b){
				String ts = m.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
				sb.insert(0, "\n" + m.getAuthor().getName() + " (" + ts + " UTC): " + m.getContent());
			}
		}

		sb.insert(0, "------- BEGIN LOG OF " + n.toUpperCase() + " (" + i + "): " + o + " -------");

		return sb.toString();
	}
}
