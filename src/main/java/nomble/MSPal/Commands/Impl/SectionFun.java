package nomble.MSPal.Commands.Impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import nomble.MSPal.Core.Bot;
import nomble.MSPal.Core.Util;
import nomble.MSPal.Data.Impl.DataSettings;
import nomble.MSPal.Commands.EnumSection;
import nomble.MSPal.Commands.ISection;
import nomble.MSPal.Commands.Helper.Impl.Rainbow;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

public class SectionFun implements ISection{
	private Bot main;

	public SectionFun(Bot m){
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
		Random r = new Random();

		int lim = 0;
		for(String[] sa : sl){
			if(lim++ > Util.getCmdLimit()){
				break;
			}

			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");
			if(c.equals("build")){
				String s = border(1);
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage(s);
				});
			}
			else if(c.equals("destroy")){
				String s = border(-1);
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage(s);
				});
			}
			else if(c.equals("pitchfork") && sa.length > 1){
				String s;
				switch((sa[1] + (sa.length > 2 ? (" " + sa[2]) : "")).toLowerCase()){
					case "traditional":
						s = "---E";
						break;
					case "left handed":
						s = "Ǝ---";
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
						s = "---€";
						break;
					case "the pound":
						s = "---£";
						break;
					case "the lira":
						s = "---₤";
						break;
					case "buckfork shotgun":
						s = "▄︻̷̿┻̿═━一E";
						break;
					case "automatic forkpitcher":
						s = "︻̷┻̿●═E";
						break;
					case "seventh division":
						s = "░░░░███████ ]--------------====-----E\n▂▄▅█M█S█P█A██▅▄▃▂\nIl███████████████████████\n◥⊙▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙◤";
						break;
					case "uber":
						s = "                 /E<>---\n<⊙============<{|⊙E<>---\n                 \\E<>---";
						break;
					default:
						s = "";
				}
				if(!s.equals("")){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage("```" + s + "```");
					});
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
										"The Euro, The Pound, AND The Lira!\n\n" +
										"TRY OUT OUR SPECIAL STOCK, FRESHLY-IMPORTED FROM FREDONIA!\n" +
										"FIRST UP, THE RUGGEDLY RELIABLE, FEATURING SEVEN-ROUND LEVER-ACTION:\n" +
										"Buckfork Shotgun!\n\n" +
										"LEGAL ONLY IN THE UNITED STATES, BOASTING A MODIFIED MINITURE MAXIM GUN:\n" +
										"Automatic Forkpitcher!\n\n" +
										"FOUND IN THE SHADIEST AFTERMARKETS, THE GERMAN-ENGINEERED:\n" +
										"Seventh Division!\n\n" +
										"TOTALLY NOT DISCOVERED IN THE NIGHTMARE REALM, THE BIGGEST AND BEST:\n" +
										"Uber!```");
				});
			}
			else if(c.equals("murder") && sa.length > 1){
				if(sa[1].contains(String.valueOf(e.getClient().getOurUser().getLongID()))){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " mysteriously disappeared!");
					});
				}
				else if(sa[1].contains(String.valueOf(e.getMessage().getAuthor().getLongID()))){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " did not reach the Nuclear Throne!");
					});
				}
				else if(r.nextInt(8) == 0){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " tripped and accidentally commited seppuku!");
					});
				}
				else{
					RequestBuffer.request(() -> {
						String[] saa = new String[sa.length - 1];
						System.arraycopy(sa, 1, saa, 0, saa.length);
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " murdered " + String.join(" ", saa) + "!");
					});
				}
			}
			else if(c.equals("shoot") && sa.length > 1){
				if(sa[1].contains(String.valueOf(e.getClient().getOurUser().getLongID()))){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " misused the Resolver!");
					});
				}
				else if(sa[1].contains(String.valueOf(e.getMessage().getAuthor().getLongID()))){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " went against BROFORCE!");
					});
				}
				else if(r.nextInt(8) == 0){
					RequestBuffer.request(() -> {
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " tripped and accidentally commited sudoku!");
					});
				}
				else{
					RequestBuffer.request(() -> {
						String[] saa = new String[sa.length - 1];
						System.arraycopy(sa, 1, saa, 0, saa.length);
						e.getMessage().getChannel().sendMessage(e.getMessage().getAuthor().mention() + " shot " + String.join(" ", saa) + "!");
					});
				}
			}
			else if(c.equals("regenbogen")){
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				try{
					ImageIO.write(Rainbow.getRainbow(), "png", bo);
				}
				catch (IOException ex){
					ex.printStackTrace();
				}
				InputStream is = new ByteArrayInputStream(bo.toByteArray());
				ic.sendFile("",is,"SANDMAN.png");
			}
		}
	}
	
	private String border(long m){
		DataSettings ds = main.getData(DataSettings.class);
		long w = m;
		try{
			w = Long.valueOf(ds.getSetting("wall")) + m;
		}
		catch(NumberFormatException e){}
		ds.setSetting("wall", String.valueOf(w));
		String s;
		switch((int)w){ // java uwot why does this have to be an int you nonce
			case -39600:
				s = " Take that, Russia!";
				break;
			case -666:
				s = " This moat goes to hell and back.";
				break;
			case 0:
				s = " **ALERT:** The border is unguarded!";
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
		return "The " + (w < 0 ? "moat" : "wall") + " just got 1 foot " + (m < 0 ? (w < 0 ? "deeper" : "shorter") : (w < 0 ? "shallower" : "taller")) + "! It is now " + Math.abs(w) + " " + (Math.abs(w) == 1 ? "foot" : "feet") + " " + (w < 0 ? "deep" : "tall") + "." + s;
	}
	
	@Override
	public String[][] getInfo(long c){
		return new String[][] {{"build", "We will build a wall, and everybody else will pay for it."},
			{"destroy", "We will dig a moat, so that none shall border glorious 'MURICA."},
			{"murder", "You're dead, kid. Takes 1 argument: the target."},
			{"shoot", "We've got guns with six senses. Takes 1 argument: the target."},
			{"pitchfork", "Enact revenge upon op! Takes 0 or 1 arguments: use none to list potential arguments."},
			{"regenbogen", "Somewhere. Fun fact: put 'bogen' between the prefix and suffix for any command in the Reactions section to become bogen'd. 'bogold' also is a thing for the old modifier."}};
	}

	@Override
	public String[] desc(){
		return new String[] {"confetti_ball", EnumSection.FUN.toString(), "Fun commands.", ":confetti_ball:", ""};
	}

	@Override
	public void load(){}
}
