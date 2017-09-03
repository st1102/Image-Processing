import java.io.*;
import hpkg.fund.pnm.*;

public class prog3_4
{
    public static void main(String[] args)
    {
	try
	    {
	    // コマンドライン引数を解析する.
		if(args.length != 4)
		    {
		System.err.println("java prog3_4 入力画像.ppm 出力画像.ppm");
		System.exit(0);
		    }

		HPnm y1Img = new HPnm();
		y1Img.readVoxels(args[0]); //入力y画像
		HPnm c1Img = new HPnm();
		c1Img.readVoxels(args[1]); //入力c1画像
		HPnm c2Img = new HPnm();
		c2Img.readVoxels(args[2]); //入力c2画像
	    
		int ysize = c1Img.ysize();
		int xsize = c1Img.xsize();
		
		HPnm rgbImg = new HPnm(xsize, ysize, 32);  // 結果画素サイズは 32 Bit Per Pixel.
		
		for(int y = 0; y < ysize; y++) {
		    for(int x = 0; x < xsize; x++) {
			int y1 = y1Img.getUnsignedValue(x, y);
			int c1_dash = c1Img.getUnsignedValue(x, y);
			int c2_dash = c2Img.getUnsignedValue(x, y);
			
			int c1 = (int)(c1_dash * (179 + 179)/255 - 179 + 0.5);//変換前に戻す + 0.5で四捨五入
			int c2 = (int)(c2_dash * (226 + 226)/255 - 226 + 0.5);
			
			int r = c1 + y1;
			if(r > 255){ //255以上となった時は255とする
			    r = 255;
			}
			int b = c2 + y1;
			if(b > 255){
			    b = 255;
			}
			int g = (int)((y1 - 0.299*r - 0.114*b)/0.587);
			if(g > 255){
			    g = 255;
			}

			//r,g,bをマージ
			int value = (0xff & r) + (0xff00 & (g << 8)) + (0xff0000 & (b << 16));
			
			rgbImg.setValue(x, y, value);
		    }
		}
		
		rgbImg.writeVoxels(args[3]);
	    }
	catch(Exception e)
	    {
		e.printStackTrace();
	    }
    }
}

