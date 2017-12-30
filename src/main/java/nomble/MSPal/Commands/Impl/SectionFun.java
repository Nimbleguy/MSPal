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

public class SectionFun implements ISection{
	private Bot main;

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

		for(String[] sa : sl){
			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");
			if(c.equals("build")){
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage(border(1));
				}
			}
			else if(c.equals("destroy")){
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage(border(-1));
				}
			}
			else if(c.equals("pitchfork") && sa.length > 1){
				String s;
				switch((sa[1] + (sa.length > 2 ? (" " + sa[2]) : "")).toLowerCase()){
					case "traditional":
						s = "---E";
						break;
					case "left handed":
						s = "";
						break;
					case "fancy":
						s = "---{";
						break;
					case "forked price":
						s = "-E";
						break;
					case "33% off":
						s = "---F";
						break;
					case "66% off":
						s = "---L";
						break;
					case "manufacturer's defect":
						s = "---e";
						break;
					case "the euro":
						s = "";
						break;
					case "the pound":
						s = "";
						break;
					case "the lira":
						s = "";
						break;
					case "buckfork shotgun":
						s = "";
					case "automatic forkpitcher":
						s = "";
						break;
					case "seventh division":
						s = "";
						break;
				}
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage("```" + s + "```");
				}
			}
			else if(c.equals("pitchfork")){
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage("```ANGRY AT OP? WANT TO JOIN THE MOB? WE'VE GOT YOU COVERED!\n" +
										"COME ON DOWN TO THE PITCHFORK EMPORIUM! WE'VE GOT 'EM ALL!\n\n" +
										"Traditional, Left Handed, AND Fancy!\n\n" +
										"WE EVEN HAVE DISCOUNTED CLEARENCE FORKS!\n" +
										"Forked Price, 33% Off, 66% Off, AND Manufacturer's Defect!\n\n" +
										"NEW IN STOCK: DIRECTLY FROM LIECHTENSTEIN, EUROPEAN MODELS!\n" +
										"The Euro, The Pound, AND The Lira!```");
				}
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage("```TRY OUT OUR SPECIAL STOCK, FRESHLY-IMPORTED FROM FREDONIA!\n" +
										"FIRST UP, THE RUGGEDLY RELIABLE, FEATURING SEVEN-ROUND LEVER-ACTION:\n" +
										"Buckfork Shotgun!\n\n" +
										"LEGAL ONLY IN THE UNITED STATES, BOASTING A MODIFIED MINITURE MAXIM GUN:\n" +
										"Automatic Forkpitcher!\n\n" +
										"FOUND IN THE SHADIEST AFTERMARKETS, THE GERMAN-ENGINEERED:\n" +
										"Seventh Division!\n\n```");
				}
			}
		}
	}
	
	private String border(long mod){
		DataSettings ds = bot.getData(DataSettings.class);
		long w = Long.valueOf(ds.getSetting("wall")) + mod;
		ds.setSetting("wall", (String)w);
		String s;
		switch(w){
			case -39600:
				s = " Take that, Russia!";
				break;
			case -666:
				s = " This moat goes to hell and back.";
				break;
			case 0:
				s = " The border is unguarded!";
				break;
			case 42:
				s = " Nobody is getting over this wall.";
				break;
			case 618:
				s = " A yellow triangle has appeared on top of the wall.";
				break;
			case 1453:
				s = " Ottoman cannons can't melt our walls!";
				break;
			default:
				s = "";
				break;
		}
		return "The " + (w < 0 ? "moat" : "wall") + " is now " + w + " feet " + (w < 0 ? "moat" : "wall") + "." + s;
	}
	
	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"build", "We will build a wall, and everybody else will pay for it."},
			{"destroy", "We will dig a moat, so that none shall border glorious 'MURICA."},
			{"pitchfork", "Enact revenge upon op! Takes 0 or 1 arguments: use none to list potential arguments."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"confetti_ball", EnumSection.FUN.toString(), "Fun commands.", ":confetti_ball:", ""};
	}

	@Override
	public void load(){}
}
