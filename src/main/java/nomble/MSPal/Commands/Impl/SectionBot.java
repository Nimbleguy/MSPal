package nomble.MSPal.Commands.Impl;

import com.vdurmont.emoji.EmojiManager;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.Impl.DataConsent;
import nomble.MSPal.Data.Impl.DataGuild;
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
		long l = -1;
		if(!(e.getChannel() instanceof IPrivateChannel)){
			l = e.getGuild().getLongID();
		}

		long al = e.getAuthor().getLongID();
		List<String[]> sl = Util.getCommand(e.getMessage().getContent(), l);
		IChannel ic = e.getMessage().getChannel();

		int lim = 0;
		for(String[] sa : sl){
			if(lim++ > Util.getCmdLimit()){
				break;
			}

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
				RequestBuffer.request(() -> {
					ic.sendMessage("You have opted " + (b ? "out of" : "into") + " user data storage.");
				});
			}
			else if(c.equals("cmdban") && sa.length > 1 && (l == -1 || e.getAuthor().getPermissionsForGuild(e.getMessage().getGuild()).contains(Permissions.MANAGE_MESSAGES)) && !sa[1].contains("|")){
				DataGuild dg = main.getData(DataGuild.class);
				boolean b = true;
				String s = dg.getCommandBans(l);
				if(s != null && s.contains(sa[1] + "|")){
					b = false;
				}
				dg.setCommandBan(l, sa[1], b);
				final boolean fb = b;
				RequestBuffer.request(() -> {
					ic.sendMessage("You have " + (fb ? "banned" : "unbanned") + " " + sa[1] + ".");
				});
			}
			else if(c.equals("reload") && al == Util.getOwner()){ // only for the bot owner
				for(ISection is : main.getSections()){
					is.load();
				}
			}
			else if (c.equals("say") && al == Util.getOwner()){ // only for bot owner
				IChannel ics = e.getClient().getChannelByID(Long.valueOf(sa[1]));
				RequestBuffer.request(() -> {
					ics.sendMessage(e.getMessage().getContent().split(" ", 3)[2]);
				});
			}
		}
	}
	
	@EventSubscriber
	public void onReact(ReactionAddEvent e){
		long l = -1;
		if(!(e.getChannel() instanceof IPrivateChannel)){
			l = e.getGuild().getLongID();
		}

		if(e.getUser().getLongID() != e.getClient().getOurUser().getLongID()){
			for(IEmbed ie : e.getMessage().getEmbeds()){
				if(ie != null && ie.getAuthor() != null){
					if(ie.getAuthor().getName().equals(EID)){
						EnumSection es = EnumSection.fromString(ie.getTitle().split(" ")[0]);
						if(es != null){
							ISection is = null;
							switch(es){
								case GENERAL:
									for(ISection isi : main.getSections()){
										if(e.getReaction().getEmoji().getName().equals(EmojiManager.getForAlias(isi.desc()[3]).getUnicode())){
											is = isi;
											break;
										}
									}
									
									if(is != null){
										EmbedObject eo = getSectionEmbed(l, is, 0);
										
										IMessage m = RequestBuffer.request(() -> {
											return e.getMessage().edit(eo);
										}).get();
										
										RequestBuffer.request(() -> {
											m.removeAllReactions();
										}).get();
										
										int p = is.getInfo(l).length / 25;
										String[] n = new String[] {":zero:", ":one:", ":two:", ":three:", ":four:",
														":five:", ":six:", ":seven:", ":eight:", ":nine:"};
										for(int i = 1; i < p + 1; i++){
											final int fi = i;
											RequestBuffer.request(() -> {
												m.addReaction(EmojiManager.getForAlias(n[fi]));
											});
										}
										
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
										}).get();
										
										for(ISection isi : main.getSections()){
											RequestBuffer.request(() -> {
												m.addReaction(EmojiManager.getForAlias(isi.desc()[3]));
											});
										}
									}
									else{
										for(ISection isi : main.getSections()){
											if(ie.getTitle().equals(isi.desc()[1])){
												is = isi;
												break;
											}
										}

										String[] n = new String[] {":zero:", ":one:", ":two:", ":three:", ":four:",
														":five:", ":six:", ":seven:", ":eight:", ":nine:"};
										for(int i = 0; i < 10; i++){
											if(e.getReaction().getEmoji().getName().equals(EmojiManager.getForAlias(n[i]).getUnicode())){
												EmbedObject eo = getSectionEmbed(l, is, i);
												
												IMessage m = RequestBuffer.request(() -> {
													return e.getMessage().edit(eo);
												}).get();
												
												RequestBuffer.request(() -> {
													m.removeAllReactions();
												}).get();
												
												int p = is.getInfo(l).length / 25;
												for(int ii = 0; ii < p + 1; ii++){
													if(ii != i){
														final int fii = ii;
														RequestBuffer.request(() -> {
															m.addReaction(EmojiManager.getForAlias(n[fii]));
														});
													}
												}
												
												RequestBuffer.request(() -> {
													m.addReaction(EmojiManager.getForAlias(":back:"));
												});
											}
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
	private EmbedObject getSectionEmbed(long l, ISection is, int n){
		EmbedBuilder b = new EmbedBuilder();
		Random r = new Random(l);
		b.withAuthorName(EID);
		b.withColor(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		b.withTitle(is.desc()[1]);
		b.withDescription(is.desc()[4]);
		
		int p = is.getInfo(l).length / 25;
		n = Math.min(p, n);
		int m = Math.min(is.getInfo(l).length, 25 * (n + 1));
		
		for(String[] sa : Arrays.copyOfRange(is.getInfo(l), 25 * n, m)){
			if(sa != null && sa[0] != null && sa[1] != null){
				b.appendField(sa[0] + "\t\t", sa[1], false);
			}
		}
		
		return b.build();
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
		
		for(ISection is : main.getSections()){
			b.appendField(":" + is.desc()[0] + ": " + is.desc()[1] + " Commands\t", is.desc()[2] + "\t", true);
		}
		
		b.appendField("Don't trust me?", "Check my [github](https://github.com/Nimbleguy/MSPal).", false);
		b.appendField("Want a bot invite?", "Here's the [link](https://discordapp.com/oauth2/authorize?client_id=208370298636992513&scope=bot&permissions=101440).", false);
		b.appendField("Want bot news?", "Join the [discord](https://discord.gg/bCSD6cz).", false);
		b.appendField("Want to support me?", "Send Dogecoin to DQMWGNPseXnHVAcRwfQvP8cG2VqkF5napc.", false);

		b.withFooterText("Made by Nomble#8128 | Now on " + e.getClient().getGuilds().size() + " Guilds!");

		return b.build();
	}
	
	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"help", "View this menu."}, {"commands", "Alias for help."}, {"info", "Alias for help."},
			{"toggleopt", "Toggle opt-in status for user data storage (stores user id). This is used only for command functionality, and only explicitly stated information is kept."},
			{"cmdban", "Toggle prevention of a command from being executed (stores guild id). You must have manage messages permissions. Takes 1 argument: the command without it's prefix and suffix."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"desktop", EnumSection.BOT.toString(), "General commands and info.", ":desktop_computer:", ""};
	}

	@Override
	public void load(){}
}
