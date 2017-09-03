import java.io.*;
import hpkg.fund.pnm.*;

public class YccToSh
{
    public static void main(String[] args)
    {
	try
	    {
	    // コマンドライン引数を解析する.
		if(args.length != 4)
	    {
		System.err.println("java prog3_2 入力画像.ppm 出力画像.ppm");
		System.exit(0);
	    }

	    HPnm inImg1 = new HPnm();
	    HPnm inImg2 = new HPnm();
	    inImg1.readVoxels(args[0]); //c1画像
	    inImg2.readVoxels(args[1]); //c2画像

	    int y1size = inImg1.ysize(); //画像のサイズ
	    int x1size = inImg1.xsize();


	    HPnm sImg = new HPnm(x1size, y1size, 8);  // 画素サイズは 8 Bit Per Pixel.
	    HPnm hImg = new HPnm(x1size, y1size, 8);  // 画素サイズは 8 Bit Per Pixel.

	    for(int y = 0; y < y1size; y++) {
		for(int x = 0; x < x1size; x++) {
		    int c1_dash = inImg1.getUnsignedValue(x, y);
		    int c2_dash = inImg2.getUnsignedValue(x, y);

		    double c1 = c1_dash * (179 + 179)/255 - 179; //c'からcへ戻す

		    double c2 = c2_dash * (226 + 226)/255 - 226;

		    double s = Math.sqrt(c1*c1 + c2*c2);

		    double h = Math.atan2(c1, c2);

		    int s_dash = (int)(s/288 * 255); //0 <= s <= 288から変換

		    int h_dash = (int)((h + Math.PI)/(Math.PI*2) * 255); //-π <= h <= πから変換

		    sImg.setValue(x, y, s_dash);
		    hImg.setValue(x, y, h_dash);
		}
	    }

	    sImg.writeVoxels(args[2]);
	    hImg.writeVoxels(args[3]);

	    }
	catch(Exception e)
	    {
	    e.printStackTrace();
	    }
    }
}
