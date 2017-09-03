import java.io.*;
import hpkg.fund.voxels.*;
import hpkg.fund.pnm.*;

public class Sobel {
  private static HPnm smooth(HPnm img, int fsize)
  throws HVoxelMakingFailureException, HUnknownDepthSpecifiedException{
      HPnm smImg = new HPnm(img.xsize(), img.ysize(), 32); // 結果画像.
      for(int y=fsize/2; y<img.ysize()-fsize/2; y++) {
        for(int x=fsize/2; x<img.xsize()-fsize/2; x++) {
          // 画素数と画素値和を計算する.
          int xd, yd, num = 0;
          double sum = 0;
          for(yd=-fsize/2; yd<=fsize/2; yd++) {
            for(xd=-fsize/2; xd<=fsize/2; xd++) {
              sum += img.getUnsignedValue(x+xd, y+yd);
              num++;
            }
          }
          // 平均値を (x,y) の値にする.
          smImg.setValue(x, y, (int)Math.rint(sum/num));
        }
      }
      return smImg;
    }

 public static void main(String[] args) {
	try {
	    if(args.length != 2) {
		      System.err.println("java edge 入力画像.pgm 出力画像.pgm");
		        System.exit(0);
      }
	    HPnm inImg = new HPnm(); // 入力画像
	    inImg.readVoxels(args[0]);

	    int ysize = inImg.ysize();
	    int xsize = inImg.xsize();

      HPnm inImgSM = smooth(inImg, 3); // 平滑化する

      int[][] hori = {{-1,0,1},
                      {-2,0,2},
                      {-1,0,1}}; //水平方向
      int[][] vert = {{-1,-2,-1},
                      {0,0,0},
                      {1,2,1}}; //垂直方向

      HPnm outImg = new HPnm(xsize, ysize, 8); // 出力画像

      for(int y = 0; y < ysize; y++){
        for(int x = 0; x < xsize; x++){
          if(x == 0 || y == 0 || x == xsize-1 || y == ysize-1){
            int value = inImgSM.getUnsignedValue(x,y);
            outImg.setValue(x,y,value);
          } else {
            int sumh = 0; //水平方向合計
            int sumv = 0; //垂直方向合計
            for(int j = -1; j < 2; j++){
              for(int i = -1; i < 2; i++){
                int value = inImgSM.getUnsignedValue(x+i,y+j);
                sumh += value * hori[1+i][1+j];
                sumv += value * vert[1+i][1+j];
              }
            }
            int newvalue = (int)(Math.sqrt(sumh*sumh + sumv*sumv));
            outImg.setValue(x,y,newvalue);
          }
        }
      }
      outImg.writeVoxels(args[1]);

  } catch(Exception e) {
	    e.printStackTrace();
  }
 }
}
