package nomble.MSPal.Commands.Impl;

import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;

import nomble.MSPal.Core.Util;
import nomble.MSPal.Commands.EnumSection;
import nomble.MSPal.Commands.ISection;
import nomble.MSPal.Commands.Helper.Impl.Rainbow;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.*;

public class SectionReaction implements ISection{

	private HashMap<List<String>, File> imgs;
	private HashMap<List<String>, String> desc;

	private Pattern repeat = Pattern.compile("^(.+?)\\1+$");

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

			String r = c.replaceAll("^top", "").replaceAll("^low", "").replaceAll("^pot", "").replaceAll("^wol", "").replaceAll("top$", "").replaceAll("low$", "").replaceAll("pot$", "").replaceAll("wol$", "").replaceAll("bogen", "");;
			String p = r;
			Matcher m = repeat.matcher(r);
			if(m.find()){
				p = m.group(1);
			}

			List<String> ls = Arrays.asList(p);
			if(imgs.get(ls) == null){
				ls = Arrays.asList(p, Long.toString(l));
			}

			if(imgs.get(ls) == null){
				continue;
			}

			File tf = imgs.get(ls);
			boolean ch = false;
			BufferedImage bf = null;
			try{
				bf = ImageIO.read(tf);
				if(tf != null && FilenameUtils.getExtension(tf.getName()).equals("png") && !(c.equals(r) && p.equals(r))){
					int s = bf.getWidth(null);
					if(bf.getHeight(null) < 100){
						s = (s * 9) / 43;
					}
					int t = r.length() / p.length();
					BufferedImage nf = new BufferedImage(bf.getWidth(null) + (s * (t - 1)), bf.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics g = nf.getGraphics();
					for(int i = 0; i < t; i++){
						g.drawImage(bf, i * s, 0, null);
					}
					g.dispose();
					bf = nf;

					if(c.contains("bogen")){
						BiFunction<Integer, Integer, Integer> lm;
						int o = new Random().nextInt(6);

						switch(o){
							case 0:
								lm = (a, b) -> a ^ b;
								break;
							case 1:
								lm = (a, b) -> a & b;
								break;
							case 2:
								lm = (a, b) -> a | b;
								break;
							case 3:
								lm = (a, b) -> (a + b) % 16777215;
								break;
							case 4:
								lm = (a, b) -> (a - b) % 16777215;
								break;
							case 5:
								lm = (a, b) -> (a * b) % 16777215;
								break;
							default:
								lm = (a, b) -> a;
								break;
						}

						BufferedImage bb = Rainbow.getRainbow();
						for(int x = 0; x < bf.getWidth(); x++){
							for(int y = 0; y < bf.getHeight(); y++){
								bf.setRGB(x, y, lm.apply(bf.getRGB(x, y), bb.getRGB(x % bb.getWidth(), y % bb.getHeight())));
							}
						}
					}
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
						};
						LookupOp lo = new LookupOp(lt, new RenderingHints(null));
						bf = lo.filter(bf, null);
					}
					ch = true;
				}

			}
			catch(IOException ee){
				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendMessage("The timeimage commited a timecrime to it has to do the timecrime in timeerrorland.");
				});
			}

			if(bf != null){
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				try{
					ImageIO.write(bf, "png", bo);
				}
				catch (IOException ex){
					ex.printStackTrace();
				}
				InputStream is = new ByteArrayInputStream(bo.toByteArray());

				RequestBuffer.request(() -> {
					e.getMessage().getChannel().sendFile("", is, "SANDMAN.png");
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
		return new String[] {"slight_smile", EnumSection.REACTION.toString(), "For all your reactions.", ":slightly_smiling:", "Put multiple of the same reactions within the prefix and suffix to get more reactions. Prefix and suffix the reaction name(s) with top, pot, low, and wol for Fun Things(tm)."};
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
