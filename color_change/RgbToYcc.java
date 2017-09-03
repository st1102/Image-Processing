import java.io.*;
import hpkg.fund.pnm.*;

public class RgbToYcc
{
    public static void main(String[] args)
    {
	try
	    {
	    // コマンドライン引数を解析する.
		if(args.length != 4)
	    {
		System.err.println("java prog3_1 入力画像.ppm 出力画像.ppm");
		System.exit(0);
	    }

	    HPnm inImg = new HPnm();
	    inImg.readVoxels(args[0]);

	    int ysize = inImg.ysize();
	    int xsize = inImg.xsize();

	    HPnm y1Img = new HPnm(xsize, ysize, 8);  // 画素サイズは 8 Bit Per Pixel.
	    HPnm c1_2Img = new HPnm(xsize, ysize, 8);  // 画素サイズは 8 Bit Per Pixel.
	    HPnm c2_2Img = new HPnm(xsize, ysize, 8);  // 画素サイズは 8 Bit Per Pixel.
	    for(int y = 0; y < ysize; y++) {
		for(int x = 0; x < xsize; x++) {
		    int value  = inImg.getUnsignedValue(x, y);
		    double red =  value & 0xff; // 赤値の取得.
		    double green = (value >> 8) & 0xff; // 緑値の取得.
		    double blue = (value >> 16) & 0xff; // 青値の取得.

		    double y1 =  0.299*red + 0.587*green + 0.114*blue;  // 輝度
		    double c1 =  0.701*red - 0.587*green - 0.114*blue; // 色差信号１
		    double c2 =  -0.299*red - 0.587*green + 0.886*blue; // 色差信号２

		    int y1_2 = (int)(y1 + 0.5); // + 0.5で四捨五入して輝度をint型へ変換
		    int c1_2 = (int)(((c1 + 179) / (179 + 179)) * 255 + 0.5);
		    //桁あふれを防ぐ処理 + 0.5で四捨五入
		    int c2_2 = (int)(((c2 + 226) / (226 + 226)) * 255 + 0.5);

		    y1Img.setValue(x, y, y1_2);
		    c1_2Img.setValue(x, y, c1_2);
		    c2_2Img.setValue(x, y, c2_2);

		}
	    }

	    y1Img.writeVoxels(args[1]);
	    c1_2Img.writeVoxels(args[2]);
	    c2_2Img.writeVoxels(args[3]);
	    }
	catch(Exception e)
	    {
	    e.printStackTrace();
	    }
    }
}
