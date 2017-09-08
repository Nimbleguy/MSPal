package nomble.MSPal.Commands.Impl;

import com.vdurmont.emoji.EmojiManager;

import java.util.List;
import java.util.Random;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Commands.ISection;

import sx.blah.discord.api.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

public class SectionBot implements ISection{
	private IDiscordClient bot;
	private Bot main;

	public SectionBot(IDiscordClient b, Bot m){
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
			if(c.equals("help") || c.equals("commands") || c.equals("info")){
				IChannel ic = e.getMessage().getChannel();
				IMessage m = RequestBuffer.request(() -> {
					EmbedBuilder b = new EmbedBuilder();
					Random r = new Random(l);
					b.withAuthorName(":mspa: help");
					b.withColor(r.nextInt(256), r.nextInt(256), r.nextInt(256));
					b.withTitle("General");

					if(ic instanceof IPrivateChannel || ic.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.ADD_REACTIONS)){
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

					b.withFooterText("Made by Nomble#8128 | Now on " + e.getClient().getGuilds().size() + " Guilds!");

					return ic.sendMessage(b.build());
				}).get();

				for(ISection is : main.getSections()){
					RequestBuffer.request(() -> {
						m.addReaction(ReactionEmoji.of(is.desc()[3]));
					});
				}
			}
		}
	}

	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"help", "View this menu."}, {"commands", "Alias for help."}, {"info", "Alias for help."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"desktop", "Bot", "General commands and info.", ":desktop_computer:"};
	}

	@Override
	public void load(){}
}
