package nomble.MSPal.Commands.Impl;

import com.vdurmont.emoji.EmojiManager;

import java.util.List;
import java.util.Random;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.Impl.DataConsent;
import nomble.MSPal.Commands.EnumSection;
import nomble.MSPal.Commands.ISection;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.*;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

public class SectionBot implements ISection{
	private Bot main;
	
	private final String EID = ":mspa: help";

	public SectionBot(Bot m){
		load();
		main = m;
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent e){
		List<String[]> sl = Util.getCommand(e.getMessage().getContent());
		long l = e.getMessage().getGuild().getLongID();
		long al = e.getAuthor().getLongID();
		IChannel ic = e.getMessage().getChannel();

		for(String[] sa : sl){
			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");
			if(c.equals("help") || c.equals("commands") || c.equals("info")){
				EmbedObject eo = getBaseEmbed(l, ic, e.getClient().getOurUser());
				IMessage m = RequestBuffer.request(() -> {
					return ic.sendMessage(eo);
				}).get();

				for(ISection is : main.getSections()){
					RequestBuffer.request(() -> {
						m.addReaction(EmojiManager.getForAlias(is.desc()[3]));
					});
				}
			}
			else if(c.equals("toggleopt")){
				DataConsent du = main.getData(DataConsent.class);
				boolean b = du.getConsent(e.getAuthor().getLongID());
				du.setConsent(e.getAuthor().getLongID(), !b);
				ic.sendMessage("You have opted " + (b ? "out of" : "into") + " user data storage.");
			}
			else if(c.equals("reload") && al == Util.getOwner()){ // only for the bot owner
				for(ISection is : main.getSections()){
					is.load();
				}
			}
		}
	}
	
	@EventSubscriber
	public void onReact(ReactionAddEvent e){
		long l = e.getGuild().getLongID();
		if(e.getUser().getLongID() != e.getClient().getOurUser().getLongID()){
			for(IEmbed ie : e.getMessage().getEmbeds()){
				if(ie != null){
					if(ie.getAuthor().getName().equals(EID)){
						EnumSection es = EnumSection.fromString(ie.getTitle().split(" ")[0]);
						if(es != null){
							switch(es){
								case GENERAL:
									ISection is = null;
									for(ISection isi : main.getSections()){
										if(e.getReaction().getEmoji().getName().equals(EmojiManager.getForAlias(isi.desc()[3]).getUnicode())){
											is = isi;
										}
									}
									
									if(is != null){
										EmbedBuilder b = new EmbedBuilder();
										Random r = new Random(l);
										b.withAuthorName(EID);
										b.withColor(r.nextInt(256), r.nextInt(256), r.nextInt(256));
										b.withTitle(is.desc()[1]);
										b.withDescription(is.desc()[4]);
										
										int i = 1;
										for(String[] sa : is.getInfo(e.getGuild().getLongID())){
											b.appendField(sa[0] + "\t", sa[1] + "\t", i++ % 3 != 0);
										}
										
										EmbedObject eo = b.build();
										IMessage m = RequestBuffer.request(() -> {
											return e.getMessage().edit(eo);
										}).get();
										
										RequestBuffer.request(() -> {
											m.removeAllReactions();
										});
										
										RequestBuffer.request(() -> {
											m.addReaction(EmojiManager.getForAlias(":back:"));
										});
									}
									break;
								default:
									if(e.getReaction().getEmoji().getName().equals(EmojiManager.getForAlias(":back:").getUnicode())){
										EmbedObject eo = getBaseEmbed(l, e.getChannel(), e.getClient().getOurUser());
										IMessage m = RequestBuffer.request(() -> {
											return e.getMessage().edit(eo);
										}).get();
										
										RequestBuffer.request(() -> {
											m.removeAllReactions();
										});
										
										for(ISection isi : main.getSections()){
											RequestBuffer.request(() -> {
												m.addReaction(EmojiManager.getForAlias(isi.desc()[3]));
											});
										}
									}
									break;
							}
						}
					}
				}
			}
		}
	}

	private EmbedObject getBaseEmbed(long l, IChannel ic, IUser e){
		EmbedBuilder b = new EmbedBuilder();
		Random r = new Random(l);
		b.withAuthorName(EID);
		b.withColor(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		b.withTitle(EnumSection.GENERAL.toString());

		if(ic instanceof IPrivateChannel || ic.getModifiedPermissions(e).contains(Permissions.ADD_REACTIONS)){
			b.withDescription("To go to a category, select the corresponding reaction.\n");

			if(!Util.getPrefix(l).equals("")){
				b.appendDescription("Current command prefix: **" + Util.getPrefix(l) + "**. ");
			}
			if(!Util.getSuffix(l).equals("")){
				b.appendDescription("Current command suffix: **" + Util.getSuffix(l) + "**. ");
			}

			b.appendDescription("\nCommand format: " + Util.getPrefix(l) + "command" + Util.getSuffix(l) + " [argument 1] [argument 2] [argument 3]...");
			b.appendDescription("\nTo get message ids, open discord settings, go to appearance, and enable developer mode. " +
				"You will then be able to copy message ids in the same interface as the options to pin and delete messages.");
		}
		else{
			b.withDescription("Please use this command in a DM, as reaction permissions are disabled on this channel.");
		}

		int i = 1;
		for(ISection is : main.getSections()){
			b.appendField(":" + is.desc()[0] + ": " + is.desc()[1] + " Commands\t", is.desc()[2] + "\t", i++ % 3 != 0);
		}
		
		b.appendField("Don't trust me?", "Check my [github](https://github.com/Nimbleguy/MSPal).", false);
		b.appendField("Want a bot invite?", "Here's the [link](temp).", false);
		b.appendField("Want bot news?", "Join the [discord](temp).", false);

		b.withFooterText("Made by Nomble#8128 | Now on " + e.getClient().getGuilds().size() + " Guilds!");

		return b.build();
	}
	
	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"help", "View this menu."}, {"commands", "Alias for help."}, {"info", "Alias for help."},
			{"toggleopt", "Toggle opt-in status for user data storage (stores user id). This is used only for command functionality, and only explicitly stated information is kept."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"desktop", EnumSection.BOT.toString(), "General commands and info.", ":desktop_computer:", ""};
	}

	@Override
	public void load(){}
}
