import java.io.*;
import hpkg.fund.pnm.*;
import hpkg.tk.ImageFilter.*;

public class template_matching {
    public static void rotate(HPnm inImg, HPnm outImg, double rad){
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

		    outImg.setValue(X, Y, 300);
		} else {
		    int value = inImg.getUnsignedValue(x_dash, y_dash);
		    outImg.setValue(X, Y, value);

		}
	    }
	}
    }

    public static void main(String[] args) {
	try {
	    if(args.length != 2) {
		System.err.println("java prog3A 入力画像 (全体).pgm 入力画像 (テンプレート).pgm");
		System.exit(0);
	    }
	    HPnm inImg = new HPnm(); // 全体画像
	    inImg.readVoxels(args[0]);
	    HPnm inImgTMPL = new HPnm(); // テンプレート画像
	    inImgTMPL.readVoxels(args[1]);

	    int ysize = inImg.ysize();
	    int xsize = inImg.xsize();

	    int ysizeTMPL = inImgTMPL.ysize();
	    int xsizeTMPL = inImgTMPL.xsize();

	    int xMin=0, yMin=0;
	    double valueDiffMin = Integer.MAX_VALUE;

	    double count = 0; //300でない画素数

	    for (int theta = 0; theta < 360; theta+=15){
		double rad = theta/180 * Math.PI;
		HPnm rotatedImg = new HPnm(xsizeTMPL, ysizeTMPL, 8);
		rotate(inImgTMPL, rotatedImg, rad); //回転メソッドを実行

		// 全体画像に部分画像を重ね，対応する画素の差の絶対値和を計算する.
		// 絶対値和が最小になる位置を対応する位置とする.
		for(int y=ysizeTMPL/2; y<ysize-ysizeTMPL/2; y++){ // インデックスに注意.
		    for(int x=xsizeTMPL/2; x<xsize-xsizeTMPL/2; x++){
			double valueDiff = 0;
			for(int yt=-ysizeTMPL/2; yt<ysizeTMPL/2; yt++){ // インデックスに注意.
			    for(int xt=-xsizeTMPL/2; xt<xsizeTMPL/2; xt++){
				int value = inImg.getUnsignedValue(x+xt, y+yt);
				int valueTMPL = rotatedImg.getUnsignedValue(xt+xsizeTMPL/2, yt+ysizeTMPL/2);

				if(valueTMPL != 300){
				    valueDiff += Math.abs(value - valueTMPL); // 絶対値
				    count++;
				}

			    }
			}
			valueDiff = valueDiff / count;

			if(valueDiff < valueDiffMin) {
			    valueDiffMin = valueDiff;
			    xMin = x;
			    yMin = y;
			}
			count = 0;

		    }
		}
	    }
	    System.out.println("(x, y) = (" + xMin + ", " + yMin + ")");
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
    }
}
