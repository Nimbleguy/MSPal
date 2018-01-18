package nomble.MSPal.Commands.Helper.Impl;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Rainbow {
	public static BufferedImage getRainbow(){
		Random r = new Random();
		double reps = Math.random()*2+1;
		BufferedImage bi = new BufferedImage(r.nextInt(2000)+200, r.nextInt(1000)+100, BufferedImage.TYPE_INT_RGB);//uses one byte per rgb data, 3 bytes per pixel
		double[] rgb = {255,0,0};
		int phase = 0;
		double[] initrgb = rgb.clone();//equal to rgb, but not a pointer to it
		int initphase = phase;//primitive data type; no need to clone
		double change;
		change = (255*6*reps)/(bi.getWidth()+bi.getHeight()-2);
		for (int y = 0; y<bi.getHeight(); y++){
			for (int x = 0; x<bi.getWidth(); x++){
				bi.setRGB(x, y, ((int)rgb[0]<<16) | ((int)rgb[1]<<8) | (int)rgb[2]);
				phase=next(phase,rgb,change);
			}

			initphase = next(initphase,initrgb,change);
			rgb=initrgb.clone();
			phase=(int)Math.abs(initphase+Math.random()>0.1?(Math.random()-.5)*10:0);
		}
		return bi;
	}

	private static int next(int phase, double[] rgb, double change){
		int affect;
		if (phase%2==0){//adding
			affect = (phase/2+1)%3;
			if (rgb[affect]>=255){
				next(++phase,rgb,change);
				return phase;
			}else{
				rgb[affect]=Math.min(255, rgb[affect]+change);
				return phase;
			}
		}else{//subbing
			affect = (phase/2)%3;
			if (rgb[affect]<=0){
				next(++phase,rgb,change);
				return phase;
			}else{
				rgb[affect]=Math.max(0, rgb[affect]-change);
				return phase;
			}
		}
	}
}
