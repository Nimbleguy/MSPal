import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.kennedyoliveira.pastebin4j.*;

import com.google.code.chatterbotapi.*;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.util.Random;
import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;
import java.util.Collections;
import java.net.URL;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

import javax.imageio.ImageIO;

//https://jitpack.io/com/github/austinv11/Discord4j/dev-2.5.3-g903dc0c-38/javadoc/index.html

public class Mspa{

	public static IDiscordClient bot;
	public static Mspa me;

	public static String last = "";

	public static HashMap<String, String[]> logs;

	public static String owner;
	public static String pastebin;
	public static String lock = "";
	public static String lock2 = "";

	public static HashMap<String, String> joaje;

	public static List<String> cat;
	public static List<String> dog;

	public static ChatterBotSession chat;

	public static Pattern keks = Pattern.compile(":((top)|(low)|k|e|k)+:");

	public static HashMap<String, List<String>> bans;

	public Mspa(String token, String own, String bin){
		try{
			owner = own;
			pastebin = bin;
			chat = new ChatterBotFactory().create(ChatterBotType.CLEVERBOT).createSession();
			bot = new ClientBuilder().withToken(token).login();
			bot.getDispatcher().registerListener(this);
			File f = new File("./logs");
			if(f.exists()){
				FileInputStream fin = new FileInputStream(f);
				ObjectInputStream oin = new ObjectInputStream(fin);
				logs = (HashMap<String, String[]>)oin.readObject();
				oin.close();
				fin.close();
			}
			else{
				f.createNewFile();
			}
			f = new File("./joaje");
			if(f.exists()){
				FileInputStream fin = new FileInputStream(f);
				ObjectInputStream oin = new ObjectInputStream(fin);
				joaje = (HashMap<String, String>)oin.readObject();
				oin.close();
				fin.close();
			}
			else{
				f.createNewFile();
			}
			f = new File("./cat");
			if(f.exists()){
				FileInputStream fin = new FileInputStream(f);
				ObjectInputStream oin = new ObjectInputStream(fin);
				cat = Collections.synchronizedList((List<String>)oin.readObject());
				oin.close();
				fin.close();
			}
			else{
				f.createNewFile();
			}
			f = new File("./dog");
			if(f.exists()){
				FileInputStream fin = new FileInputStream(f);
				ObjectInputStream oin = new ObjectInputStream(fin);
				dog = Collections.synchronizedList((List<String>)oin.readObject());
				oin.close();
				fin.close();
			}
			else{
				f.createNewFile();
			}
			f = new File("./bans");
			if(f.exists()){
				FileInputStream fin = new FileInputStream(f);
				ObjectInputStream oin = new ObjectInputStream(fin);
				bans = (HashMap<String, List<String>>)oin.readObject();
				oin.close();
				fin.close();
			}
			else{
				f.createNewFile();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(logs == null){
			logs = new HashMap<String, String[]>();
		}
		if(joaje == null){
			joaje = new HashMap<String, String>();
		}
		if(cat == null){
			cat = Collections.synchronizedList(new ArrayList<String>());
		}
		if(dog == null){
			dog = Collections.synchronizedList(new ArrayList<String>());
		}
		if(bans == null){
			bans = new HashMap<String, List<String>>();
		}
	}

	public static void main(String[] args){
		if(args.length == 0){
			System.out.println("The bot token needs to be an arg.");
			return;
		}
		if(args.length == 1){
			me = new Mspa(args[0], "162345113966608394", null);
		}
		else if(args.length == 3){
			me = new Mspa(args[0], args[1], args[2]);
		}
		else if(args.length == 5){
			lock = args[3];
			lock2 = args[4];
			me = new Mspa(args[0], args[1], args[2]);
		}
		else{
			me = new Mspa(args[0], args[1], null);
		}
	}

	public void beginFacting(){
		Calendar cal = Calendar.getInstance();
		long now = cal.getTimeInMillis();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long start = cal.getTimeInMillis();
		//Acts on UTC.
		long delay = 43200000 - ((now - start) % 43200000);
		new Timer("catfacts", true).schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					Gson g = new Gson();
					synchronized(cat){
						URL web = new URL("http://catfacts-api.appspot.com/api/facts?number=" + cat.size());
						InputStream in = web.openStream();
						Facts fat = g.fromJson(IOUtils.toString(in, "utf-8"), Facts.class);
						in.close();
						if(fat.getSuccess()){
							int index = 0;
							Iterator<String> i = cat.iterator();
							while(i.hasNext()){
								IUser user = bot.getUserByID(i.next());
								if(user != null){
									bot.getOrCreatePMChannel(user).sendMessage(":cat:" + fat.getFacts()[index] + ":cat:");
									index++;
								}
							}
						}
					}
					synchronized(dog){
						Iterator<String> i = dog.iterator();
						while(i.hasNext()){
							IUser user = bot.getUserByID(i.next());
							if(user != null){
								if(new Random().nextInt(2) == 0){
									bot.getOrCreatePMChannel(user).sendMessage(":dog: https://www.youtube.com/watch?v=8veTn8YZ0_E :dog:");
								}
								else{
									bot.getOrCreatePMChannel(user).sendMessage(":dog: https://www.youtube.com/watch?v=E7WD3sxG8j0 :dog:");
								}
							}
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}, delay, 43200000); //1/2 day in ms
	}

	public void watchdog(){
		new Timer("watchdog", true).schedule(new TimerTask() {
			@Override
			public void run(){
				try{
					if(!bot.isReady()){
						System.exit(1);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

		}, 60000, 60000);
	}

	@EventSubscriber
	public void ready(ReadyEvent e){
		System.out.println("MSPAL ONLINE");
		bot.changePresence(false);
		bot.changeStatus(Status.game(":commands:"));
		beginFacting();
		watchdog();
	}

	@EventSubscriber
	public void edit(MessageUpdateEvent e){
		if(e.getOldMessage().getAuthor().isBot() || e.getNewMessage() == null || e.getNewMessage().getContent() == null){
			return;
		}
		try{
			String auth = e.getOldMessage().getAuthor().getName();
			IChannel chan = e.getOldMessage().getChannel();
			String oldMsg = e.getOldMessage().getContent();
			String newMsg = e.getNewMessage().getContent();
			if(logs.containsKey(chan.getID()) && logs.get(chan.getID()) != null){
				String[] info = logs.get(chan.getID());
				File t = new File(info[0] + ".txt");
				FileInputStream fin = new FileInputStream(t);
				List<String> l = IOUtils.readLines(fin, "utf-8");
				fin.close();
				int line = l.lastIndexOf(auth + ": " + oldMsg);
				if(line != -1){
					String thing = "";
					l.set(line, auth + ": " + newMsg);
					t.delete();
					t.createNewFile();
					PrintWriter out = new PrintWriter(new FileOutputStream(t, true));
					for(String str : l){
						out.println(str);
					}
					out.close();
				}
			}
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}

	@EventSubscriber
	public void message(MessageReceivedEvent e){
		if(e.getMessage().getAuthor().isBot() || e.getMessage() == null || e.getMessage().getContent() == null){
			return;
		}
		if(!(e.getMessage().getChannel() instanceof IPrivateChannel) && !e.getMessage().getChannel().getModifiedPermissions(bot.getOurUser()).contains(Permissions.READ_MESSAGES)){
			return;
		}
		if(bans.get(e.getMessage().getChannel().getID()) != null && bans.get(e.getMessage().getChannel().getID()).contains(e.getMessage().getAuthor().getID())){
			return;
		}
		try{
			String msg = e.getMessage().getContent();
			IChannel chan = e.getMessage().getChannel();
			Matcher matchkek = keks.matcher(msg);
			if(chan instanceof IPrivateChannel){
				last = msg;
			}

			if(msg.equals(":endlog:")){
				String[] f = logs.get(chan.getID());
				logs.put(chan.getID(), null);
				File t = new File(f[0] + ".txt");
				chan.sendFile(t);
				if(pastebin != null){
					PasteBin paste = new PasteBin(new AccountCredentials(pastebin));
					Paste p = new Paste();
					p.setTitle(bot.getUserByID(f[1]).getName() + " Log");
					p.setExpiration(PasteExpiration.NEVER);
					p.setVisibility(PasteVisibility.PUBLIC);
					p.setHighLight(PasteHighLight.TEXT);
					FileInputStream fin = new FileInputStream(t);
					p.setContent(IOUtils.toString(fin, "utf-8"));
					fin.close();
					chan.sendMessage(paste.createPaste(p));
				}
				t.delete();
			}

			if(logs.containsKey(chan.getID()) && logs.get(chan.getID()) != null){
				String[] info = logs.get(chan.getID());
				MessageList l = chan.getMessages();
				PrintWriter out = new PrintWriter(new FileOutputStream(new File("./" + info[0] + ".txt"), true));
				if(!l.getLatestMessage().getID().equals(e.getMessage().getID())){
					Stack<IMessage> rev = new Stack<IMessage>();
					for(IMessage m : l){
						if(m.getID().equals(info[2])){
							break;
						}
						rev.push(m);
					}
					while(!rev.empty()){
						IMessage m = rev.pop();
						out.println(m.getAuthor().getName() + ": " + m.getContent());
					}
				}
				out.println(e.getMessage().getAuthor().getName() + ": " + msg);
				out.close();
				logs.put(chan.getID(), new String[] {info[0], info[1], e.getMessage().getID()});
			}

			if(msg.equals(":startlog:")){
				if(!(logs.containsKey(chan.getID()) && logs.get(chan.getID()) != null)){
					String[] v = new String[] {e.getMessage().getID(), e.getMessage().getAuthor().getID(), e.getMessage().getID()};
					logs.put(chan.getID(), v);
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("Log started.");
				}
				else{
					String u = bot.getUserByID(logs.get(chan.getID())[1]).getName();
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("There's already a log in effect by " + u + ".");
				}
			}

			if(msg.equals(":::") && e.getMessage().getAuthor().getID().equals(owner)){
				chan.sendMessage(last);
				e.getMessage().delete();
			}
			if(msg.contains(":mspa:")){
				chan.sendFile(new File("./mspa.png"));
			}
			if(msg.contains(":olliesouty:")){
				chan.sendFile(new File("./olliesouty.png"));
			}
			if(msg.contains(":journal:")){
				chan.sendFile(new File("./journal.png"));
			}
//			if(msg.contains(":topkek:") || msg.contains(":kek:")){
//				chan.sendFile(new File("./kek.png"));
//			}
			if(msg.contains(":emily") && !(chan instanceof IPrivateChannel) && (chan.getGuild().getID().equals(lock2) || chan.getGuild().getID().equals(lock))){
				chan.sendFile(new File("./emily.png"));
			}
			if(msg.contains(":marriage:")){
				chan.sendMessage("MSPal says: :marridge:");
			}
			if(msg.contains(":marridge:")){
				chan.sendMessage("That's not how the word is spelled.");
			}
			if(msg.contains(":haigb:")){
				chan.sendFile(new File("./joaje.png"));
			}
			if(msg.contains(":su:") || msg.contains(":Steven:")){
				chan.sendFile(new File("./su.png"));
			}
//			if(msg.contains(":lowkek:")){
//				chan.sendFile(new File("./lowkek.png"));
//			}
			if(msg.contains(":ech:")){
				chan.sendFile(new File("./ech.png"));
			}
			if(msg.contains(":what:")){
				chan.sendFile(new File("./what.png"));
			}
			if(msg.contains(":highqualityrips:")){
				chan.sendMessage("**Ｉ  ｏｎｌｙ  ｕｐｌｏａｄ  ｈｉｇｈ－ｑｕａｌｉｔｙ  ｖｉｄｅｏ  ｇａｍｅ  ｒｉｐｓ．**");
			}
			if(msg.contains(":yee:")){
				chan.sendFile(new File("./yee.png"));
			}
			if(msg.contains(":ham:")){
				chan.sendFile(new File("./ham.png"));
			}
			if(msg.contains(":deathstare:")){
				chan.sendFile(new File("./deathstare.png"));
			}
			if(msg.contains(":scatman:")){
				chan.sendMessage("https://www.youtube.com/watch?v=y6oXW_YiV6g");
			}
			if(msg.contains(":boi:")){
				chan.sendFile(new File("./boi.png"));
			}
			if(msg.contains(":squids:")){
				chan.sendFile(new File("./squids.png"));
			}
			if(msg.contains(":faceplam:")){
				chan.sendFile(new File("./facepalm.png"));
			}
			if(msg.contains(":rules:")){
				chan.sendFile(new File("./rules.png"));
			}
			if(msg.contains(":fun:")){
				chan.sendFile(new File("./fun.png"));
			}
			if(msg.contains(":salt:")){
				chan.sendFile(new File("./salt.png"));
			}
			if(msg.contains(":kappa:")){
				chan.sendFile(new File("./kappa.png"));
			}
			if(msg.contains(":shame:")){
				chan.sendMessage("To the Shame Corner with <@" + e.getMessage().getAuthor().getID() + ">.");
			}
			if(msg.contains(":tom:")){
				chan.sendFile(new File("./tom.png"));
			}
			if(msg.contains(":fear:")){
				chan.sendFile(new File("./fear.png"));
			}
			if(msg.contains(":pain:")){
				chan.sendFile(new File("./pain.png"));
			}
			if(msg.contains(":jerry:")){
				chan.sendFile(new File("./jerry.png"));
			}
			if(msg.contains(":trump:")){
				int x = new Random().nextInt(5)+1;
				chan.sendFile(new File("./trump" + x + ".png"));
			}
//			if(msg.contains(":kektop:")){
//				chan.sendFile(new File("./kektop.png"));
//			}
//			if(msg.contains(":keklow:")){
//				chan.sendFile(new File("./keklow.png"));
//			}
			if(msg.equals(":pitchfork:")){
				chan.sendMessage("```ANGRY AT OP? WANT TO JOIN THE MOB? WE'VE GOT YOU COVERED!\n"
						+ "COME ON DOWN TO THE :pitchfork: EMPORIUM\n"
						+ "WE'VE GOT 'EM ALL!\n"
						+ "Traditional --E\n"
						+ "Left Handed Ǝ---\n"
						+ "Fancy ---{\n"
						+ "WE EVEN HAVE DISCOUNTED CLEARENCE FORKS!\n"
						+ "Forked Price -E\n"
						+ "33% Off ---F\n"
						+ "66% Off ---L\n"
						+ "Manufacturer's Defect ---e\n"
						+ "DON'T FORGET OUR SPECIAL STOCK!\n"
						+ "Automatic ︻┻●═E\n"
						+ "Pitchfork ---E---E\n"
						+ "Boring [REDACTED]\n"
						+ "NEW IN STOCK. DIRECTLY FROM LIECHTENSTEIN. EUROPEAN MODELS!\n"
						+ "The Euro ---€\n"
						+ "The Pound ---£\n"
						+ "The Lira ---₤\n"
						+ "HAPPY LYNCHING!\n"
						+ "*Some assembly required```");
			}
			else if(msg.startsWith(":pitchfork: ")){
				String buy = msg.replace(":pitchfork: ", "");
				if(buy.equalsIgnoreCase("Traditional")){
					chan.sendMessage("---E");
				}
				else if(buy.equalsIgnoreCase("Left Handed")){
					chan.sendMessage("Ǝ---");
				}
				else if(buy.equalsIgnoreCase("Fancy")){
					chan.sendMessage("---{");
				}
				else if(buy.equalsIgnoreCase("33% Off")){
					chan.sendMessage("---F");
				}
				else if(buy.equalsIgnoreCase("66% Off")){
					chan.sendMessage("---L");
				}
				else if(buy.equalsIgnoreCase("Manufacturer's Defect")){
					chan.sendMessage("---e");
				}
				else if(buy.equalsIgnoreCase("The Euro")){
					chan.sendMessage("---€");
				}
				else if(buy.equalsIgnoreCase("The Pound")){
					chan.sendMessage("---£");
				}
				else if(buy.equalsIgnoreCase("The Lira")){
					chan.sendMessage("---₤");
				}
				else if(buy.equalsIgnoreCase("Automatic")){
					chan.sendMessage("︻̷┻̿●═E");
				}
				else if(buy.equalsIgnoreCase("Pitchfork")){
					chan.sendMessage("---E---E");
				}
				else if(buy.equalsIgnoreCase("Boring")){
					chan.sendFile(new File("boring.png"));
				}
				else if(buy.equalsIgnoreCase("Forked Price")){
					chan.sendMessage("-E");
				}
				else{
					chan.sendMessage("We don't have that pitchfork in stock.");
				}
			}
			if(!(chan instanceof IPrivateChannel) && chan.getGuild().getID().equals(lock)){
				if(msg.contains(":bone:")){
					chan.sendFile(new File("./bone.gif"));
				}
				if(msg.contains(":rip:")){
					chan.sendFile(new File("./rip.png"));
				}
				if(msg.contains(":kerpranked:")){
					chan.sendFile(new File("./kerpranked.png"));
				}
				if(msg.contains(":mimeowl:")){
					chan.sendMessage("**:/: O) /_\\\\ (O :\\:**");
				}
				if(msg.contains(":pls:")){
					chan.sendFile(new File("./pls.png"));
				}
				if(msg.equals(":themage:")){
					FileInputStream fin = new FileInputStream(new File("./themage"));
                                	List<String> lines = IOUtils.readLines(fin, "utf-8");
                                	fin.close();
                                	int r = new Random().nextInt(lines.size());
                                	chan.sendMessage("At least you didn't say " + lines.get(r) + ".");
				}
			}
			if(joaje.containsKey(chan.getID()) && joaje.get(chan.getID()) != null){
				if(msg.toLowerCase().contains("hear a joke")){
					chan.sendMessage("My ex-wife still misses me...");
					chan.sendMessage("But her aim is getting better!");
					chan.sendMessage("See, it's funny because marridge is terrible.");
				}
				if(msg.toLowerCase().contains("still misses me")){
					chan.sendMessage("But her aim is getting better!");
				}
				if(msg.toLowerCase().contains("waluigi")){
					chan.sendMessage("**WALUIGI IS BACK. NOW THE CHAT IS FUNNY AGAIN.**");
				}
				if(msg.toLowerCase().contains("spooky scary skeletons")){
					chan.sendMessage("https://www.youtube.com/watch?v=q6-ZGAGcJrk");
				}
				if(msg.toLowerCase().contains("give") && msg.toLowerCase().contains("lemons")){
					chan.sendMessage("Don’t make lemonade. Make life take the lemons back! Get mad! I don’t want your damn lemons, what the hell am I supposed to do with these? Demand to see life’s manager! Make life rue the day it thought it could give Cave Johnson lemons! Do you know who I am? I’m the man who’s gonna burn your house down! With the lemons! I’m gonna get my engineers to invent a combustible lemon that burns your house down!");
				}
				if(msg.toLowerCase().contains("interject")){
					chan.sendMessage("I'd just like to interject for a moment. What you’re referring to as Linux, is in fact, GNU/Linux, or as I’ve recently taken to calling it, GNU plus Linux. Linux is not an operating system unto itself, but rather another free component of a fully functioning GNU system made useful by the GNU corelibs, shell utilities and vital system components comprising a full OS as defined by POSIX. Many computer users run a modified version of the GNU system every day, without realizing it. Through a peculiar turn of events, the version of GNU which is widely used today is often called “Linux”, and many of its users are not aware that it is basically the GNU system, developed by the GNU Project. There really is a Linux, and these people are using it, but it is just a part of the system they use. Linux is the kernel: the program in the system that allocates the machine’s resources to the other programs that you run. The kernel is an essential part of an operating system, but useless by itself; it can only function in the context of a complete operating system. Linux is normally used in combination with the GNU operating system: the whole system is basically GNU with Linux added, or GNU/Linux. All the so-called “Linux” distributions are really distributions of GNU/Linux.");
				}
				if(msg.toLowerCase().contains("arstotzka")){
					chan.sendMessage("Glory to Arstotzka!");
				}
				if(msg.toLowerCase().contains("w h a t w e r e a l l y a r e")){
					chan.sendMessage("https://www.youtube.com/watch?v=yOr9fztiiT8");
				}
			}

			if(msg.equals(":commands:")){
				IPrivateChannel pm = bot.getOrCreatePMChannel(e.getMessage().getAuthor());
				pm.sendMessage("```:mspa: mspal\n"
						+ ":olliesouty: olly out\n"
						+ ":journal: >be stan\n"
						+ ":haigb: >be stan 2: electric boogaloo\n"
						+ ":commands: why did I need to say this\n"
						+ ":startlog: start a chatlog\n"
						+ ":endlog: end a chatlog\n"
						+ ":invite: invite link\n"
						+ ":cue: the most accurate 8ball\n"
						+ ":joaje: allowed users may toggle jokes in the current channel\n"
						+ ":kek: topkek\n"
						+ ":topkek: literally :kek:\n"
						+ ":marriage: what do you think\n"
						+ ":origin: where did the mspal come from?\n"
						+ ":ask: what does the mspal's pet say\n"
						+ ":pitchfork: want to join the mob\n"
						+ ":su: heil mspal, your lord and gem\n"
						+ ":murder: you egg\n"
						+ ":lowkek: ech\n"
						+ ":ech: jantran\n"
						+ ":zodiac: but you made one fatal mistake, you messed with my family\n"
						+ ":what: wat u playin at boi\n"
						+ ":highqualityrips: A S T H E T I C\n"
						+ ":yee: soos and dinosaur bros\n"
						+ ":kektop: memes are going to kill us all one day\n"
						+ ":keklow: you just wait\n"
						+ "there's a dynamic kek generator, the keks have won the war. all that's left is death.\n"
						+ ":ham: it's a meme now\n"
						+ ":deathstare: for when flowey is your b e s t f r i e n d\n"
						+ ":info: mspal info\n"
						+ ":catfacts: most cats adore sardines\n"
						+ ":hievery1imnew!!!!!!!holdsupsporkmynameiskatybutucancallmet3hPeNgU1NoFd00m!!!!!!!!lol…asucanseeimveryrandom!!!!thatswhyicamehere,2meetrandomppllikeme_…im13yearsold(immature4myagetho!!)ilike2watchinvaderzimw/mygirlfreind(imbiifudontlikeitdealw/it)itsourfavoritetvshow!!!bcuzitsSOOOOrandom!!!!shesrandom2ofcoursebutiwant2meetmorerandomppl=)liketheysaythemorethemerrier!!!!lol…newaysihope2makealotoffreindsheresogivemelotsofcommentses!!!!DOOOOOMMMM!!!!!!!!!!!!!!!!<---mebeinrandomagain_^hehe…toodles!!!!!loveandwaffles,t3hPeNgU1NoFd00m: what you're referring as linux is, in fact, gnu/linux\n"
						+ ":scatman: john\n"
						+ ":boi: dat boi is d e a d\n"
						+ ":faceplam: at least it isn't death\n"
						+ ":squids: you're a kid now\n"
						+ ":rules: abide by them or p e r i s h\n"
						+ ":fun: u is for uranium bombs\n"
						+ ":salt: square, armrest, and saltshaker\n"
						+ ":kappa: twitch installs mspal\n"
						+ ":shame: the corner of shame\n"
						+ ":dogfacts: dogs like to absorb artifacts```");
				pm.sendMessage("```:whoa: f u n k y f r e s h\n"
						+ ":tom: wanna have a bad tom\n"
						+ ":fear: s r p e l o\n"
						+ ":pain: o l e p r s\n"
						+ ":jerry: and jerry came too\n"
						+ ":ignore: makes the bot userist, only works with mentions```");
				if(!(chan instanceof IPrivateChannel) && chan.getGuild().getID().equals(lock)){
					pm.sendMessage("```:rip: i can't believe america is dead\n"
							+ ":bone: the prize is a bone\n"
							+ ":themage: тхе маге\n"
							+ ":kerpranked: with pd kerprank\n"
							+ ":mimeowl: hail, the most fearsome of conflict shards and ██████. the end of ends ever looms.\n"
							+ ":pls: the edits```");
				}
				if(!(chan instanceof IPrivateChannel) && (chan.getGuild().getID().equals(lock2) || chan.getGuild().getID().equals(lock))){
					pm.sendMessage("```:emily: the dream```");
				}
			}
			else if(msg.equals(":away:") && e.getMessage().getAuthor().getID().equals(owner)){
				bot.changePresence(true);
				bot.changeStatus(Status.game("Maintenance"));
			}
			else if(msg.equals(":invite:")){
				chan.sendMessage("https://discordapp.com/oauth2/authorize?client_id=208370298636992513&scope=bot&permissions=101376");
			}
			else if(msg.startsWith(":cue::cue:") || msg.startsWith(":cue: :cue:")){
				chan.sendMessage("Recursion is bad. Don't do recursion, kids.");
			}
			else if(msg.startsWith(":cue: ")){
				String cue = msg.replace(":cue: ", "");
				if(cue.toLowerCase().equals("how do balls cue?")){
					byte seed = MessageDigest.getInstance("MD5").digest(cue.getBytes("utf-8"))[0];
					Random r = new Random(seed);
					int shift = r.nextInt(10) + 2;
					String out = "";
					for(char c : "How do ball cues?".toCharArray()){
						if('A' <= c && c <= 'Z'){
							out += (char)(((c - 'A' + shift) % 26) + 'A');
						}
						else if('a' <= c && c <= 'z'){
							out += (char)(((c - 'a' + shift) % 26) + 'a');
						}
						else{
							out += c;
						}
					}
					chan.sendMessage(out);
				}
				else{
					byte seed = MessageDigest.getInstance("MD5").digest(cue.getBytes("utf-8"))[0];
					Random r = new Random(seed);
					int shift = r.nextInt(10) + 2;
					URL lorem = new URL("http://www.lipsum.com/feed/xml?what=words&amount=" + Integer.toString(shift) + "&start=no");
					InputStream in = lorem.openStream();
					String text = IOUtils.toString(in, "utf-8");
					in.close();
					text = text.replaceAll("(?im)^((?!<lipsum>).)*$", "");
					text = text.replaceAll("\n", "");
					text = text.replaceAll("<lipsum>", "");
					text = text.replaceAll("<\\/lipsum>", "");
					String out = "";
					for(char c : text.toCharArray()){
						if('A' <= c && c <= 'Z'){
							out += (char)(((c - 'A' + shift) % 26) + 'A');
						}
						else if('a' <= c && c <= 'z'){
							out += (char)(((c - 'a' + shift) % 26) + 'a');
						}
						else{
							out += c;
						}
					}
					chan.sendMessage(out);
				}
			}
			else if(msg.startsWith(":ask: ")){
				String s = msg.replace(":ask: ", "");
				String out = chat.think(s);
				if(s.toLowerCase().contains("what") && s.toLowerCase().contains("your name")){
					out = "MSPal.";
				}
				chan.sendMessage(s.equalsIgnoreCase("What's your favorite idea?") ? "Mine is being creative." : out);
			}
			else if(msg.equals(":joaje:") && chan.getModifiedPermissions(e.getMessage().getAuthor()).contains(Permissions.MANAGE_MESSAGES)){
				if(joaje.containsKey(chan.getID()) && joaje.get(chan.getID()) != null){
					joaje.put(chan.getID(), null);
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("joaje disabled");
				}
				else{
					joaje.put(chan.getID(), "");
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("joaje enabled");
				}
			}
			else if(msg.equals(":origin:")){
				FileInputStream fin = new FileInputStream(new File("./origin"));
				List<String> lines = IOUtils.readLines(fin, "utf-8");
				fin.close();
				int r = new Random().nextInt(lines.size());
				chan.sendMessage(lines.get(r));
			}
			else if(msg.startsWith(":murder: ")){
				int sodo = new Random().nextInt(25);
				if(msg.toLowerCase().contains(bot.getOurUser().mention()) || msg.toLowerCase().contains(bot.getOurUser().getID()) || (msg.toLowerCase().contains("mspa")) || msg.equalsIgnoreCase("me")){
					chan.sendMessage("no");
				}
				else if(msg.toLowerCase().contains("nomble") || msg.toLowerCase().contains("nimble") || msg.contains(owner)){
					chan.sendMessage(e.getMessage().getAuthor().mention() + " commited " + ((sodo == 0) ? "sudoku!" : "seppuku!"));
				}
				else if(msg.contains(e.getMessage().getAuthor().getID()) || msg.contains(e.getMessage().getAuthor().getName())){
					chan.sendMessage(e.getMessage().getAuthor().mention() + " commited " + ((sodo == 0) ? "sudoku!" : "seppuku!"));
				}
				else{
					if(msg.contains("208089544816459777")){
						chan.sendMessage(e.getMessage().getAuthor().mention() + " viciously tortured " + msg.replace(":murder: ", "") + " to death!");
					}
					else{
						int rand = new Random().nextInt(5);
						if(rand != 0){
							chan.sendMessage(e.getMessage().getAuthor().mention() + " brutally killed " + msg.replace(":murder: ", "") + "!");
						}
						else{
							chan.sendMessage(e.getMessage().getAuthor().mention() + " forced " + msg.replace(":murder: ", "") + " to listen to Yakety Sax until they went insane and died.");
						}
					}
				}
			}
			else if(msg.equals(":zodiac:")){
				FileInputStream fin = new FileInputStream(new File("./zodiac"));
				List<String> lines = IOUtils.readLines(fin, "utf-8");
				fin.close();
				if(!(chan instanceof IPrivateChannel) && chan.getGuild().getID().equals(lock)){
					fin = new FileInputStream(new File("./zodiaclock"));
					lines.addAll(IOUtils.readLines(fin, "utf-8"));
					fin.close();
				}
				int r = new Random().nextInt(lines.size());
				chan.sendMessage(lines.get(r));
			}
			else if(msg.equals(":newcmd:") && e.getMessage().getAuthor().getID().equals(owner)){
				chan.sendMessage("New command time.");
			}
			else if(msg.equals(":info:")){
				chan.sendMessage("A Discord4J bot made by <@162345113966608394>. Use :commands: for a commandlist.");
			}
			else if(msg.equals(":catfacts:")){
				boolean sub = false;
				synchronized(cat){
					if(cat.contains(e.getMessage().getAuthor().getID())){
						cat.remove(e.getMessage().getAuthor().getID());
					}
					else{
						cat.add(e.getMessage().getAuthor().getID());
						sub = true;
					}
				}
				if(sub){
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("You have been subscribed to :cat: Facts! Every day, you will get a new :cat: fact!");
				}
				else{
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("You have been unsubscribed to :cat: Facts...");
				}
			}
			else if(msg.equalsIgnoreCase(":hievery1imnew!!!!!!!holdsupsporkmynameiskatybutucancallmet3hPeNgU1NoFd00m!!!!!!!!lol…asucanseeimveryrandom!!!!thatswhyicamehere,2meetrandomppllikeme_…im13yearsold(immature4myagetho!!)ilike2watchinvaderzimw/mygirlfreind(imbiifudontlikeitdealw/it)itsourfavoritetvshow!!!bcuzitsSOOOOrandom!!!!shesrandom2ofcoursebutiwant2meetmorerandomppl=)liketheysaythemorethemerrier!!!!lol…newaysihope2makealotoffreindsheresogivemelotsofcommentses!!!!DOOOOOMMMM!!!!!!!!!!!!!!!!<---mebeinrandomagain_^hehe…toodles!!!!!loveandwaffles,t3hPeNgU1NoFd00m:")){
				chan.sendFile(new File("./penguin.png"));
			}
			else if(msg.equals(":dogfacts:")){
				boolean sub = false;
				synchronized(dog){
					if(dog.contains(e.getMessage().getAuthor().getID())){
						dog.remove(e.getMessage().getAuthor().getID());
					}
					else{
						dog.add(e.getMessage().getAuthor().getID());
						sub = true;
					}
				}
				if(sub){
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("You have been subscribed to :dog: Facts! Every day, you will get a new :dog: fact!");
				}
				else{
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("You have been unsubscribed to :dog: Facts...");
				}
			}
			else if(msg.startsWith(":whoa: ")){
				String s = msg.replace(":whoa: ", "");
				String fir = "";
				String las = "";
				boolean first = false;
				for(char c : s.toCharArray()){
					fir += c + " ";
					if(first){
						las += "\n" + c;
					}
					else{
						first = true;
					}
				}
				chan.sendMessage("```" + fir + las + "```");
			}
			else if(msg.startsWith(":ignore: <@") && chan.getModifiedPermissions(e.getMessage().getAuthor()).contains(Permissions.MANAGE_MESSAGES)){
				String s = msg.replace(":ignore: <@", "").replace(">", "").replace("!", "");
				if(bans.get(e.getMessage().getChannel().getID()) != null){
					if(bans.get(e.getMessage().getChannel().getID()).contains(s)){
						List<String> li = bans.get(e.getMessage().getChannel().getID());
						li.remove(s);
						bans.put(e.getMessage().getChannel().getID(), li);
						bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("<@" + s + "> has been unblocked!");
					}
					else{
						List<String> li = bans.get(e.getMessage().getChannel().getID());
						li.add(s);
						bans.put(e.getMessage().getChannel().getID(), li);
						bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("<@" + s + "> has been locked from using my commands!");
					}
				}
				else{
					List<String> li = new ArrayList<String>();
					li.add(s);
					bans.put(e.getMessage().getChannel().getID(), li);
					bot.getOrCreatePMChannel(e.getMessage().getAuthor()).sendMessage("<@" + s + "> has been locked from using my commands!");
				}
			}
			if(matchkek.find()){
				String keks = matchkek.group().replace(":", "");
				int ek = StringUtils.countMatches(keks, "ek");
				String justkek = keks.replace("top", "").replace("low", "");
				BufferedImage k = ImageIO.read(new File("./kek.png"));
				if(justkek.startsWith("e")){
					LookupTable colors = new LookupTable(0, 4){
						@Override
						public int[] lookupPixel(int[] src, int[] dest){
							dest[0] = (int)(255 - src[0]);
							dest[1] = (int)(255 - src[1]);
							dest[2] = (int)(255 - src[2]);
							return dest;
						}
					};
					LookupOp colop = new LookupOp(colors, new RenderingHints(null));
					k = colop.filter(k, null);
					ek = StringUtils.countMatches(keks, "ke");
				}
				if(keks.startsWith("low")){
					AffineTransform at = AffineTransform.getScaleInstance(1, -1);
					at.translate(0, -k.getHeight(null));
					AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					k = ato.filter(k, null);
				}
				else if(keks.endsWith("top")){
					AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
					at.translate(-k.getWidth(null), 0);
					AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					k = ato.filter(k, null);
				}
				else if(keks.endsWith("low")){
					AffineTransform at = AffineTransform.getScaleInstance(-1, -1);
					at.translate(-k.getWidth(null), -k.getHeight(null));
					AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					k = ato.filter(k, null);
				}
				BufferedImage out = new BufferedImage(k.getWidth(null) + (9 * ek), k.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics g = out.getGraphics();
				if(keks.contains("low") && keks.contains("top")){
					g.setClip(new Ellipse2D.Float(0, 0, k.getWidth(null) + (9 * ek), k.getHeight(null)));
				}
				for(int i = 0; i < ek; i++){
					g.drawImage(k, i * 9, 0, null);
				}
				g.dispose();
				File outf = new File("./" + e.getMessage().getID() + ".png");
				ImageIO.write(out, "png", outf);
				chan.sendFile(outf);
				outf.delete();
			}

			FileOutputStream fout = new FileOutputStream(new File("./logs"));
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			oout.writeObject(logs);
			oout.close();
			fout.close();
			fout = new FileOutputStream(new File("./joaje"));
			oout = new ObjectOutputStream(fout);
			oout.writeObject(joaje);
			oout.close();
			fout.close();
			fout = new FileOutputStream(new File("./cat"));
			oout = new ObjectOutputStream(fout);
			oout.writeObject(cat);
			oout.close();
			fout.close();
			fout = new FileOutputStream(new File("./dog"));
			oout = new ObjectOutputStream(fout);
			oout.writeObject(dog);
			oout.close();
			fout.close();
			fout = new FileOutputStream(new File("./bans"));
			oout = new ObjectOutputStream(fout);
			oout.writeObject(bans);
			oout.close();
			fout.close();
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
	public class Facts{
		private String[] facts;
		private boolean success;

		public String[] getFacts(){
			return facts;
		}
		public boolean getSuccess(){
			return success;
		}

		@Override
		public String toString(){
			return "Facts [facts= " + facts + ", success=" + success + "]";
		}
	}
}
