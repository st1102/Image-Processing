import java.io.*;
import hpkg.fund.pnm.*;

public class RgbColorChange
{
    public static void main(String[] args)
    {
	try
	    {
	    // コマンドライン引数を解析する.
		if(args.length != 4)
	    {
		System.err.println("java prog3_5_rgb 入力画像.ppm 出力画像.ppm");
		System.exit(0);
	    }

	    HPnm inImgs = new HPnm();
	    HPnm inImgh = new HPnm();
	    HPnm inImgy = new HPnm();
	    inImgs.readVoxels(args[0]); //s画像
	    inImgh.readVoxels(args[1]); //h画像
	    inImgy.readVoxels(args[2]); //y画像

	    int s_ysize = inImgs.ysize(); //画像のサイズ
	    int s_xsize = inImgs.xsize();

	    HPnm rgbImg = new HPnm(s_xsize, s_ysize, 32);  // 画素サイズは 32 Bit Per Pixel.

	    for(int y = 0; y < s_ysize; y++) {
		for(int x = 0; x < s_xsize; x++) {
		    int s_dash = inImgs.getUnsignedValue(x, y);
		    int h_dash = inImgh.getUnsignedValue(x, y);
		    int y1 = inImgy.getUnsignedValue(x, y);

		    double s = (s_dash * 288/255); //sを変換前に戻す
		    double h = (h_dash * (Math.PI*2)/255 - Math.PI); //hを変換前に戻す

		    s = s; //sとhを変更
		      h = h + 200;

		    int new_c2 = (int)(s * Math.cos(h));
		    int new_c1 = (int)(s * Math.sin(h));

		    int r = new_c1 + y1;
		    if(r > 255){ //255以上となった時は255とする
			r = 255;
		    }
		    if(r < 0){
			r = 0;
		    }
		    int b = new_c2 + y1;
		    if(b > 255){
			b = 255;
		    }
		    if(b < 0){
			b = 0;
		    }
		    int g = (int)((y1 - 0.299*r - 0.114*b)/0.587);
		    if(g > 255){
			g = 255;
		    }
		    if(g < 0){
			g = 0;
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
