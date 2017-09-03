import java.io.*;
import hpkg.fund.voxels.*;
import hpkg.fund.pnm.*;
public class Gaussian {

private static HPnm gaussian(HPnm img)
throws HVoxelMakingFailureException, HUnknownDepthSpecifiedException{
    int xsize = img.xsize();
    int ysize = img.ysize();
    HPnm smImg = new HPnm (xsize, ysize, 8); // 結果画像.

    double[][] filter = new double[5][5];

    int sigma = 1;
    for(int y = 0; y < 5; y++){
      for(int x = 0; x < 5; x++){
        filter[x][y] = (Math.exp(-(x*x + y*y)/2*sigma*sigma))/(2*Math.PI*sigma*sigma);
      }
    }

    for(int y = 2; y < ysize-2; y++){
      for(int x = 2; x < xsize-2; x++){
        double sum = 0;
        for(int j = -2; j < 3; j++){
          for(int i = -2; i < 3; i++){
            double value = img.getUnsignedValue(x+i,y+j);
            sum += value * filter[2+i][2+j];
            //System.out.println(filter[2+i][2+j]);
          }
        }
        /*if(sum == 0){
          System.out.println(sum);
        }*/
        smImg.setValue(x,y,(int)(sum));
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

      HPnm outImg = gaussian(inImg);

      outImg.writeVoxels(args[1]);

  } catch(Exception e) {
	    e.printStackTrace();
  }
 }
}
