import java.io.*;
import hpkg.fund.pnm.*;

public class ShToNewC
{
    public static void main(String[] args)
    {
	try
	    {
	    // コマンドライン引数を解析する.
		if(args.length != 4)
	    {
		System.err.println("java prog3_3 入力画像.ppm 出力画像.ppm");
		System.exit(0);
	    }

	    HPnm inImgs = new HPnm();
	    HPnm inImgh = new HPnm();
	    inImgs.readVoxels(args[0]); //s画像
	    inImgh.readVoxels(args[1]); //h画像

	    int s_ysize = inImgs.ysize(); //画像のサイズ
	    int s_xsize = inImgs.xsize();

	    HPnm newc1Img = new HPnm(s_xsize, s_ysize, 8);  // 画素サイズは 8 Bit Per Pixel.
	    HPnm newc2Img = new HPnm(s_xsize, s_ysize, 8);  // 画素サイズは 8 Bit Per Pixel.


	    for(int y = 0; y < s_ysize; y++) {
		for(int x = 0; x < s_xsize; x++) {
		    int s_dash = inImgs.getUnsignedValue(x, y);
		    int h_dash = inImgh.getUnsignedValue(x, y);

		    double s = (s_dash * 288/255); //sを変換前に戻す
		    double h = (h_dash * (Math.PI*2)/255 - Math.PI); //hを変換前に戻す

		    double new_c2 = s * Math.cos(h);
		    double new_c1 = s * Math.sin(h);

		    int new_c1_dash = (int)((new_c1 + 179)/(179 + 179) * 255);
		    //-179 <= c1 <= 179から変換
		    int new_c2_dash = (int)((new_c2 + 226)/(226 + 226) * 255);
		    //-226 <= c2 <= 226から変換

		    newc1Img.setValue(x, y, new_c1_dash);
		    newc2Img.setValue(x, y, new_c2_dash);
		}
	    }

	    newc1Img.writeVoxels(args[2]);
	    newc2Img.writeVoxels(args[3]);

	    }
	catch(Exception e)
	    {
	    e.printStackTrace();
	    }
    }
}
