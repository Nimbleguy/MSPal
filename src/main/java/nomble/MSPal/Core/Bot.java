package nomble.MSPal.Core;

import com.github.kennedyoliveira.pastebin4j.*;

import java.util.List;
import java.util.ArrayList;

import nomble.MSPal.Section.ISection;
import nomble.MSPal.Section.Impl.*;

import sx.blah.discord.api.*;
import sx.blah.discord.api.events.*;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class Bot implements IListener<ReadyEvent>{
	private IDiscordClient bot;
	private PasteBin paste;

	private String owner;

	private List<ISection> sects;

	public Bot(String t, String o, String p){
		bot = new ClientBuilder().withToken(t).build();

		if(p != null){
			paste = new PasteBin(new AccountCredentials(p));
		}
		else{
			paste = null;
		}

		owner = o;

		sects = new ArrayList<ISection>();
	}

	public void init(){
		sects.add(new SectionReaction(bot));
		sects.add(new SectionBot(bot, this));
		if(paste != null){
			sects.add(new SectionLog(bot, this));
		}

		bot.getDispatcher().registerListener(this);
		for(ISection s : sects){
			bot.getDispatcher().registerListener(s);
		}

		bot.login();
		bot.idle("with bootstraps.");
	}

	public List<ISection> getSections(){
		return sects;
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
		e.getClient().online(":info:");
	}
}
