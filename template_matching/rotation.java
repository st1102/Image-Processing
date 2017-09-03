import java.io.*;
import hpkg.fund.pnm.*;

public class prog4_2_method {
    public static void rotate(HPnm inImg, HPnm outImg, HPnm flag, double rad){
	int ysize = inImg.ysize();
	int xsize = inImg.xsize();

	int ycenter = (int)(ysize/2 + 0.5);
	int xcenter = (int)(xsize/2 + 0.5);

	for(int X = 0; X < xsize; X++){
	    for(int Y = 0; Y < ysize; Y++){ //X,Y（回転後）
		int X_dash = (int)(X - xcenter + 0.5); //X_dash,Y_dash（中心を戻す前）
		int Y_dash = (int)(Y - ycenter + 0.5);
		int x = (int)(X_dash * Math.cos(-rad) - Y_dash * Math.sin(-rad) + 0.5); //x,y（回転前）
		int y = (int)(X_dash * Math.sin(-rad) + Y_dash * Math.cos(-rad) + 0.5);
		int x_dash = (int)(x + xcenter); //x_dash,y_dash（平行移動前）
		int y_dash = (int)(y + ycenter);
		if(x_dash < 0 || y_dash < 0 || x_dash > xsize - 1 || y_dash > ysize - 1){
		    flag.setValue(X, Y, 1);
		    outImg.setValue(X, Y, 300);
		} else {
		    int value = inImg.getUnsignedValue(x_dash, y_dash);
		    outImg.setValue(X, Y, value);
		    //System.out.println(x_dash + "" + y_dash);
		}
	    }
	}
    }

    public static void main(String[] args) {
	try {
	    if(args.length != 3) {
		System.err.println("java prog4_1 入力画像.pgm 出力画像.pgm 回転角度(0〜360)");
		System.exit(0);
	    }

	    HPnm inImg = new HPnm(); //回転前（入力）画像
	    inImg.readVoxels(args[0]); //入力画像を読み込み

	    int ysize = inImg.ysize();
	    int xsize = inImg.xsize();

	    double theta = Double.parseDouble(args[2]); //回転角度
	    double rad = theta/180.0 * Math.PI;

	    HPnm outImg = new HPnm(xsize, ysize, 16); //回転後（出力）画像
	    //outImg.readVoxels(args[0]);

	    HPnm flag = new HPnm(xsize, ysize, 8);

	    rotate(inImg, outImg, flag, rad); //回転メソッドを実行

	    outImg.writeVoxels(args[1]); //出力画像に書き込み

	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
}
