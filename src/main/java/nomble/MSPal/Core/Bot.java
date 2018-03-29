package nomble.MSPal.Core;

import com.github.kennedyoliveira.pastebin4j.*;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import nomble.MSPal.Commands.ISection;
import nomble.MSPal.Commands.Impl.*;
import nomble.MSPal.Data.IData;
import nomble.MSPal.Data.SQL;
import nomble.MSPal.Data.Impl.*;

import sx.blah.discord.api.*;
import sx.blah.discord.api.events.*;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

public class Bot implements IListener<ReadyEvent>{
	private IDiscordClient bot;
	private PasteBin paste;

	private List<ISection> sects;
	private List<IData> data;

	private String pass;

	public Bot(String t, String[] sa){
		bot = new ClientBuilder().withToken(t).build();

		String p = "";

		if((p = sa[EnumInput.PASTEBIN.ordinal()]) != null){
			paste = new PasteBin(new AccountCredentials(p));
		}
		else{
			paste = null;
		}

		if((p = sa[EnumInput.ENCRYPT.ordinal()]) != null){
			pass = p;
		}
		else{
			pass = null;
		}

		if((p = sa[EnumInput.OWNER.ordinal()]) != null){
			Util.owner = Long.valueOf(sa[EnumInput.OWNER.ordinal()]);
		}
		else{
			Util.owner = -1;
		}

		String c = "";
		if((c = sa[EnumInput.SQLADDR.ordinal()]) != null){
			if((p = sa[EnumInput.SQLPREF.ordinal()]) != null){
				Util.sqlPrefix = p;
			}
			else{
				Util.sqlPrefix = "";
			}

			data = new ArrayList<IData>();

			data.add(new DataLog());
			data.add(new DataSettings());
			data.add(new DataConsent());
			data.add(new DataUser());
			data.add(new DataGuild());

			Util.sql = new SQL(c, sa[EnumInput.SQLUSER.ordinal()], sa[EnumInput.SQLPASS.ordinal()]);
		}
		else{
			Util.sql = null;
		}

		sects = new ArrayList<ISection>();
	}

	public void init(){
		Util.bot = this;

		if(Util.sql != null){
			if(pass == null){
				System.out.println("Please enter the password to encrypt the database with.\n" +
							"You cannot change this without deleting the database.");
				pass = new Scanner(System.in).nextLine();
			}
			if(!Util.sql.password(pass)){
				System.err.println("Invalid password. Delete the database to reset it, or try entering it again.");
				System.exit(-1);
			}
			pass = null;
		}

		sects.add(new SectionReaction());
		sects.add(new SectionBot(this));
		if(paste != null){
			sects.add(new SectionLog(this));
		}
		sects.add(new SectionFun(this));

		bot.getDispatcher().registerListener(this);
		for(ISection s : sects){
			bot.getDispatcher().registerListener(s);
		}

		bot.login();
		bot.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "with bootstraps.");
	}

	public List<ISection> getSections(){
		return sects;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(Class<? extends T> c){
		for(IData d : data){
			if(d != null && c.isInstance(d)){
				return (T)d;
			}
		}

		return null;
	}

	public boolean canUpload(){
		return paste != null;
	}

	public String upload(String n, String s){
		if(paste != null){
			Paste p = new Paste();
			p.setTitle(n);
			p.setExpiration(PasteExpiration.NEVER);
			p.setHighLight(PasteHighLight.TEXT);
			p.setVisibility(PasteVisibility.UNLISTED);
			p.setContent(s);
			return paste.createPaste(p);
		}
		else{
			return null;
		}
	}

	@Override
	public void handle(ReadyEvent e){
		e.getClient().changePresence(StatusType.ONLINE, ActivityType.WATCHING, "the :info:");
	}
}
