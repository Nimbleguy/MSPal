package nomble.MSPal.Section.Impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Section.ISection;

import org.apache.commons.io.FileUtils;

import sx.blah.discord.api.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.util.*;

public class SectionReaction implements ISection{
	private IDiscordClient bot;

	private HashMap<List<String>, File> imgs;
	private HashMap<List<String>, String> desc;

	public SectionReaction(IDiscordClient b){
		load();

		bot = b;
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent e){
		List<String[]> sl = Util.getCommand(e.getMessage().getContent());
		long l = e.getMessage().getGuild().getLongID();

		for(String[] sa : sl){
			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");
			List<String> ls = Arrays.asList(c);
			if(imgs.get(ls) == null){
				ls = Arrays.asList(c, Long.toString(l));
			}

			final List<String> lsf = ls;
			if(imgs.get(lsf) != null){
				RequestBuffer.request(() -> {
					try{
						e.getMessage().getChannel().sendFile(imgs.get(lsf));
					}
					catch(FileNotFoundException fe){
						fe.printStackTrace();
					}
				});
			}
		}
	}

	@Override
	public String[][] getInfo(long c){
		String[][] sa = new String[desc.size()][2];

		int i = 0;
		for(Entry<List<String>, String> e : desc.entrySet()){
			if(e.getKey().size() == 1 || e.getKey().get(1).equals(Long.toString(c))){
				sa[i][0] = e.getKey().get(0);
				sa[i++][1] = e.getValue();
			}
		}

		return sa;
	}

	@Override
	public String[] desc(){
		return new String[] {"slight_smile", "Reaction", "For all your reactions.", ":slightly_smiling:"};
	}

	@Override
	public void load(){
		imgs = new HashMap<List<String>, File>();
		desc = new HashMap<List<String>, String>();

		try{
			File d = new File("./reactions");

			for(File f : d.listFiles()){
				if(f.isDirectory()){
					for(File ff : f.listFiles()){
						List<String> l = Arrays.asList(remExt(ff.getName()), f.getName());
						if(ff.getName().endsWith(".txt")){
							desc.put(l, FileUtils.readFileToString(ff, "UTF-8"));
						}
						else{
							imgs.put(l, ff);
						}
					}
				}
				else{
					List<String> l = Arrays.asList(remExt(f.getName()));
					if(f.getName().endsWith(".txt")){
						desc.put(l, FileUtils.readFileToString(f, "UTF-8"));
					}
					else{
						imgs.put(l, f);
					}
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private String remExt(String s){
		int l = s.lastIndexOf(".");
		if(l > 0){
			return s.substring(0, l);
		}
		return s;
	}
}
