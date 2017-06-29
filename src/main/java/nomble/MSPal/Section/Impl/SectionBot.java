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
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
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
				RequestBuffer.request(() -> {
					EmbedBuilder b = new EmbedBuilder();
					Random r = new Random(l);
					b.withAuthorName(":mspa: help");
					b.withColor(r.nextInt(256), r.nextInt(256), r.nextInt(256));
					b.withTitle("General");

					IChannel ic = e.getMessage().getChannel();
					if(ic instanceof IPrivateChannel || ic.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.ADD_REACTIONS)){
						b.withDescription("To go to a category, select the corresponding reaction.");
					}
					else{
						b.withDescription("Please use this command in a dm, as reaction permissions are disabled.");
					}

					for(ISection is : main.getSections()){
						b.appendField(is.desc()[0] + " " + is.desc()[1] + " Commands", is.desc()[2], true);
					}

					b.withFooterText("Made by Nomble#8128");

					IMessage m = ic.sendMessage(b.build());
					RequestBuffer.request(() -> {
						for(ISection is : main.getSections()){
							m.addReaction(EmojiManager.getForAlias(is.desc()[0]));
						}
					});
				});
			}
		}
	}

	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"help", "View this menu."}, {"commands", "Alias for help."}, {"info", "Alias for help."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"regional_indicator_b", "Bot", "General commands and info."};
	}

	@Override
	public void load(){}
}
