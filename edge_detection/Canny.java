import java.io.*;
import hpkg.fund.voxels.*;
import hpkg.fund.pnm.*;
public class Canny {

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

public static void sobel(HPnm inimg, HPnm intensimg, HPnm tanimg){
  //sobel法によりエッジ強度、勾配方向を計算
  int ysize = inimg.ysize();
  int xsize = inimg.xsize();

  int[][] fx = {{-1,0,1},
                {-2,0,2},
                {-1,0,1}}; //水平方向
  int[][] fy = {{-1,-2,-1},
                {0,0,0},
                {1,2,1}}; //垂直方向

  for(int y = 0; y < ysize; y++){
    for(int x = 0; x < xsize; x++){
      if(x == 0 || y == 0 || x == xsize-1 || y == ysize-1){
        int value = inimg.getUnsignedValue(x,y);
        intensimg.setValue(x,y,value);
      } else {
        int sumfx = 0; //水平方向合計
        int sumfy = 0; //垂直方向合計
        for(int j = -1; j < 2; j++){
          for(int i = -1; i < 2; i++){
            int value = inimg.getUnsignedValue(x+i,y+j);
            sumfx += value * fx[1+i][1+j];
            sumfy += value * fy[1+i][1+j];
          }
        }
        int intens = (int)(Math.sqrt(sumfx*sumfx + sumfy*sumfy)); //エッジ強度
        intensimg.setValue(x,y,intens);

        double tan = 0; //勾配方向
        if(sumfx == 0){
          tan = 0;
        } else {
          tan = sumfy/sumfx;
        }

        //勾配方向の量子化
        if(-0.4142<tan && tan <=0.4142){ //1
          tanimg.setValue(x,y,1);
        } else if (0.4142<tan && tan<2.4142){ //2
          tanimg.setValue(x,y,2);
        } else if (tan<-2.4142 || 2.4142<tan){ //3
          tanimg.setValue(x,y,3);
        } else if (-2.4142<tan && tan<=-0.4142){ //4
          tanimg.setValue(x,y,4);
        }
      }
    }
  }
}

public static void sharp(HPnm intensimg, HPnm tanimg){  //非最大値抑制
  int ysize = intensimg.ysize();
  int xsize = intensimg.xsize();
  for(int y = 1; y < ysize-1; y++){
    for(int x = 1; x < xsize-1; x++){
      int tan_num = tanimg.getUnsignedValue(x,y); //量子化後の値
      int ixy = intensimg.getUnsignedValue(x,y); //(x,y)の強度
      if(tan_num == 1){
        if(ixy <= intensimg.getUnsignedValue(x-1,y) || ixy <= intensimg.getUnsignedValue(x+1,y)){
          intensimg.setValue(x,y,0);
        }
      } else if(tan_num == 2){
        if(ixy <= intensimg.getUnsignedValue(x-1,y+1) || ixy <= intensimg.getUnsignedValue(x+1,y-1)){
          intensimg.setValue(x,y,0);
        }
      } else if(tan_num == 3){
        if(ixy <= intensimg.getUnsignedValue(x,y-1) || ixy <= intensimg.getUnsignedValue(x,y+1)){
          intensimg.setValue(x,y,0);
        }
      } else if(tan_num == 4){
        if(ixy <= intensimg.getUnsignedValue(x-1,y-1) || ixy <= intensimg.getUnsignedValue(x+1,y+1)){
          intensimg.setValue(x,y,0);
        }
      }
    }
  }
}

public static HPnm Hyster(HPnm intensimg, int high, int low) //ヒステリシスしきい値処理
throws HVoxelMakingFailureException, HUnknownDepthSpecifiedException{
  int ysize = intensimg.ysize();
  int xsize = intensimg.xsize();
  HPnm edgeImg = new HPnm(xsize, ysize, 8); // 出力画像

  for(int y = 0; y < ysize; y++){
    for(int x = 0; x < xsize; x++){
      int ixy = intensimg.getUnsignedValue(x,y); //(x,y)の強度
      if(ixy > high){
        edgeImg.setValue(x,y,ixy); //highより強度が高い画素はエッジ
      }
    }
  }

  int side = 50; //隣をみる数

  for(int y = side; y < ysize-side; y++){
    for(int x = side; x < xsize-side; x++){
      int ixy = intensimg.getUnsignedValue(x,y); //(x,y)の強度
      if(low <= ixy && ixy <= high){
        for(int i = -side; i <= side; i++){
          for(int j = -side; j <= side; j++){
            if(i != 0 && j != 0){
              if(edgeImg.getUnsignedValue(x+i,y+j) > 0 )//エッジに結合していればエッジとする
              {
                edgeImg.setValue(x,y,ixy);
              }
            }
          }
        }
      }
    }
  }
  return edgeImg;
}

 public static void main(String[] args) {
	try {
	    if(args.length != 2) {
		      System.err.println("java edge 入力画像.pgm 出力画像.pgm");
		        System.exit(0);
      }
	    HPnm inImg = new HPnm(); // 入力画像
	    inImg.readVoxels(args[0]);
      int xsize = inImg.xsize();
      int ysize = inImg.ysize();

      HPnm smImg = gaussian(inImg);

      HPnm intensImg = new HPnm(xsize, ysize, 8);
      HPnm tanImg = new HPnm(xsize, ysize, 8);
      sobel(smImg, intensImg, tanImg);

      sharp(intensImg, tanImg);

      int high = 50;
      int low = 10;
      HPnm edgeImg = Hyster(intensImg, high, low);
      edgeImg.writeVoxels(args[1]);

  } catch(Exception e) {
	    e.printStackTrace();
  }
 }
}
