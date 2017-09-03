import java.io.*;
import hpkg.fund.pnm.*;
public class laplacian {
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

      int[][] lap = {{1,1,1},
                      {1,-8,1},
                      {1,1,1}};

      HPnm outImg = new HPnm(xsize, ysize, 8); // 出力画像

      for(int y = 0; y < ysize; y++){
        for(int x = 0; x < xsize; x++){
          if(x == 0 || y == 0 || x == xsize-1 || y == ysize-1){
            int value = inImg.getUnsignedValue(x,y);
            outImg.setValue(x,y,value);
          } else {
            int sum = 0;
            for(int j = -1; j < 2; j++){
              for(int i = -1; i < 2; i++){
                int value = inImg.getUnsignedValue(x+i,y+j);
                sum += value * lap[1+i][1+j];
              }
            }
            outImg.setValue(x,y,sum);
          }
        }
      }

      outImg.writeVoxels(args[1]);

  } catch(Exception e) {
	    e.printStackTrace();
  }
 }
}
