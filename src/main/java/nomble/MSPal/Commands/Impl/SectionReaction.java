package nomble.MSPal.Commands.Impl;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Commands.EnumSection;
import nomble.MSPal.Commands.ISection;

import org.apache.commons.io.FileUtils;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.*;

public class SectionReaction implements ISection{

	private HashMap<List<String>, File> imgs;
	private HashMap<List<String>, String> desc;

	private Pattern repeat = Pattern.compile("^(.+)+?\\1+$");

	public SectionReaction(){
		load();
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent e){
		long l = -1;
		if(!(e.getChannel() instanceof IPrivateChannel)){
			l = e.getMessage().getGuild().getLongID();
		}

		List<String[]> sl = Util.getCommand(e.getMessage().getContent(), l);

		for(String[] sa : sl){
			String c = sa[0].replaceFirst("^" + Util.getPrefix(l), "").replaceFirst(Util.getSuffix(l) + "$", "");

			String r = c.replaceAll("[(top)(pot)(low)(wol)]", "");
			String p = r;
			Matcher m = repeat.matcher(r);
			if(m.find()){
				p = m.group(1);
			}

			List<String> ls = Arrays.asList(r);
			if(imgs.get(ls) == null){
				ls = Arrays.asList(r, Long.toString(l));
			}

			final List<String> lsf = ls;

			File tf = imgs.get(lsf);
			boolean c = false;
			if(tf != null && !(c.equals(r) && p.equals(r)){
				BufferedImage bf = ImageIO.read(tf);

				if(c.startsWith("low") || c.startsWith("wol")){
					AffineTransform at = AffineTransform.getScaleInstance(1, -1);
					at.translate(0, -bf.getHeight(null));
					AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					bf = ato.filter(bf, null);
				}
				if(c.endsWith("top") || c.endsWith("pot")){
					AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
					at.translate(-bf.getWidth(null), 0);
					AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					bf = ato.filter(bf, null);
				}
				else if(c.endsWith("low") || c.endsWith("wol")){
					AffineTransform at = AffineTransform.getScaleInstance(-1, -1);
					at.translate(-bf.getWidth(null), -bf.getHeight(null));
					AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					bf = ato.filter(bf, null);
				}

				if(c.contains("pot") ^ c.contains("wol")){
					LookupTable lt = new LookupTable(0, 4){
						@Override
						public int[] lookupPixel(int[] s, int[] d){
							d[0] = 255 - s[0];
							d[1] = 255 - s[1];
							d[2] = 255 - s[2];
							return d;
						}
					}
					LookupOp lo = new LookupOp(lt, new RenderingHints(null));
					bf = lo.filter(bf, null);
				}

				if(!p.equals(r)){
					int s = (bf.getWidth(null) * 9) / 43
					int t = StringUtils.countMatches(r, p);
					BufferedImage nf = new BufferedImage(bf.getWidth(null) + (s * t), bf.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics g = nf.getGraphics();
					for(int i = 0; i < t; i++){
						g.drawImage(bf, i * s, 0, null);
					}
					g.dispose();
					bf = nf;
				}

				tf = new File(System.getProperty("java.io.tmpdir") + File.pathSeperator + String.valueOf(e.getMessage().getLongID()) + ".png");
				ImageIO.write(bf, "png", tf);
				c = true;
			}

			final File f = tf;
			if(f != null){
				RequestBuffer.request(() -> {
					try{
						e.getMessage().getChannel().sendFile(imgs.get(lsf));
					}
					catch(FileNotFoundException fe){
						fe.printStackTrace();
					}
				});
			}
			if(c){
				f.delete();
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
		return new String[] {"slight_smile", EnumSection.REACTION.toString(), "For all your reactions. Put multiple of the same reactions within the prefix/suffix to get many. Prefix and suffix the reaction name with top, pot, low, and wol for Fun Things(tm).", ":slightly_smiling:", ""};
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
