import java.io.*;
import hpkg.fund.pnm.*;
public class Labeling {
    public static void main(String[] args) {
	try {
	    // コマンドライン引数を解析する.
	    if(args.length != 2)
	    {
		System.err.println("java prog2_2 入力画像.pgm 出力画像.pgm");
		System.exit(0);
	    }

	    // PGM 形式の濃淡画像を読み込む.
	    HPnm inImg = new HPnm();
	    inImg.readVoxels(args[0]);
	    // 入力した画像のサイズ xsize (横) と ysize (縦) を取得する.
	    int ysize = inImg.ysize();
	    int xsize = inImg.xsize();
	    // 出力用の画像を生成する.画像サイズは(xsize,ysize)で，
	    // 画素サイズは
	    // 32 Bit Per Pixel.
	    HPnm labelImg = new HPnm(xsize, ysize, 32);
	    int L_length = 100000; //LUTの要素数
	    int[] LUT = new int[L_length]; //LUT
	    for(int i = 0; i < L_length; i++){ //LUTを初期化
		LUT[i] = i;
	    }
	    int L_new = 1; //新規のラベル

	    // y を 0 から ysize-1 まで，x を 0 から xsize-1 までそれぞれ
	    // 1ずつ変化させて，以下の処理を繰り返す.なお，この順番に画素に
	    // アクセスすることを「ラスタスキャン」という.
	    for(int y=0; y<ysize; y++){
		for(int x=0; x<xsize; x++)
		    {
			if(x == 0 && y == 0){ //(0, 0)
			    int value = inImg.getUnsignedValue(x, y); //画素値を取得
			    if(value != 0){ //画素値が０でない
				labelImg.setValue(x, y, L_new); //新規ラベル付与
				L_new++; //新規ラベルをプラス
			    }
			} else if(x != 0 && y == 0){ //(x, 0)
			    int value = inImg.getUnsignedValue(x, y); //画素値を取得
			    if(value != 0){ //画素値が０でない
				int left_value = inImg.getUnsignedValue(x-1, y); //左の画素値
				int left_label = labelImg.getUnsignedValue(x-1, y); //左のラベルを取得
				if(left_value == 0){ //左の画素値が０
				    labelImg.setValue(x, y, L_new); //新規ラベル付与
				    L_new++; //新規ラベルをプラス
				} else { //左の画素値が０でない

				    labelImg.setValue(x, y, left_label); //左と同じラベルを付与
				}
			    }
			} else if(x == 0 && y != 0){ //(0, y)
			    int value = inImg.getUnsignedValue(x, y); //画素値を取得
			    if(value != 0){ //画素値が０でない
				int up_value = inImg.getUnsignedValue(x, y-1); //上の画素値
				if(up_value == 0){ //上の画素値が０
				    labelImg.setValue(x, y, L_new); //新規ラベル付与
				    L_new++; //新規ラベルをプラス
				} else { //上の画素値が０でない
				    int up_label = labelImg.getUnsignedValue(x, y-1); //上のラベルを取得
				    labelImg.setValue(x, y, up_label); //上と同じラベルを付与
				}
			    }
			} else if(x != 0 && y != 0){ //(x, y)
			    int value = inImg.getUnsignedValue(x, y); //画素値を取得
			    if(value != 0){ //画素値が０でない
				int left_value = inImg.getUnsignedValue(x-1, y); //左の画素値
				int up_value = inImg.getUnsignedValue(x, y-1); //上の画素値
				if(left_value == 0 && up_value == 0){ //左と上の画素値が０
				    labelImg.setValue(x, y, L_new); //新規ラベル付与
				    L_new++; //新規ラベルをプラス
				} else if (left_value != 0 && up_value == 0){ //左の画素値だけ０でない
				    int left_label = labelImg.getUnsignedValue(x-1, y); //左のラベルを取得
				    labelImg.setValue(x, y, left_label); //左と同じラベルを付与
				} else if (left_value == 0 && up_value != 0){ //上の画素値だけ０でない
				    int up_label = labelImg.getUnsignedValue(x, y-1); //上のラベルを取得
				    labelImg.setValue(x, y, up_label); //上と同じラベルを付与
				} else if (left_value != 0 && up_value != 0){ //左と上の画素値が０でない
				    int left_label = labelImg.getUnsignedValue(x-1, y); //左のラベルを取得
				    int up_label = labelImg.getUnsignedValue(x, y-1); //上のラベルを取得
				    if(left_label == up_label){ //左と上のラベルが同じ
					labelImg.setValue(x, y, left_label); //同じラベルを付与
				    } else { //左と上のラベルが違う
					if(left_label < up_label){ //左のラベルの方が小さい
					    labelImg.setValue(x, y, left_label); //左と同じラベルを付与
					    for(int s = 1; s < L_new; s++){ //LUT[1]からLUT[L_new-1]まで
						if(LUT[s] == LUT[up_label]){ //LUT[max]と同じなら
						    LUT[s] = LUT[left_label]; //LUT[min]に変更
						}
					    }
					} else { //上のラベルの方が小さい
					    labelImg.setValue(x, y, up_label); //上と同じラベルを付与
					    for(int s = 1; s < L_new; s++){ //LUT[1]からLUT[L_new-1]まで
						if(LUT[s] == LUT[left_label]){ //LUT[max]と同じなら
						    LUT[s] = LUT[up_label]; //LUT[min]に変更
						}
					    }
					}
				    }
				}
			    }
			}
		    }
	    }

	    for(int y=0; y<ysize; y++){
		for(int x=0; x<xsize; x++)
		    {
			int value = labelImg.getUnsignedValue(x, y); //画素値を取得
			if(value != 0){
			    if(LUT[value] != value){
				labelImg.setValue(x, y, LUT[value]);
			    }
			}
		    }
	    }

	    int[] area = new int[L_new]; //面積を格納する配列
	    for(int i = 1; i < L_new; i++){ //面積を計算
		for(int y=0; y<ysize; y++) {
		    for(int x=0; x<xsize; x++){
			int value = labelImg.getUnsignedValue(x, y);
			if(value == i){
			    area[i]++;
			}
		    }
		}
	    }

	    int[] peri = new int[L_new]; //周囲長を格納する配列
	    for(int i = 1; i < L_new; i++){ //周囲長を計算
		for(int y=0; y<ysize; y++) {
		    for(int x=0; x<xsize; x++){
			int value = labelImg.getUnsignedValue(x, y);
			if(x == 0 || y == 0 || x == xsize-1 || y == ysize-1){ //端
			    if(value == i){
				peri[i]++;
			    }
			} else { //端でない
			    int left_value = labelImg.getUnsignedValue(x-1, y);
			    int right_value = labelImg.getUnsignedValue(x+1, y);
			    int up_value = labelImg.getUnsignedValue(x, y-1);
			    int down_value = labelImg.getUnsignedValue(x, y+1);
			    if(value == i){
				if(left_value == 0 || right_value == 0 || up_value == 0 ||down_value == 0) {
				    peri[i]++;
				}
			    }
			}
		    }
		}
	    }

	    double[] circle = new double[L_new]; //円形度を格納する配列
	    for(int i = 1; i < L_new; i++){
		if(area[i] != 0 && peri[i] != 0){
		    circle[i] = (4*Math.PI*area[i])/(peri[i]*peri[i]);
		}
	    }

	    int[] sum_x = new int[L_new]; //x座標の合計
	    int[] sum_y = new int[L_new]; //y座標の合計
	    double[] cen_gra_x = new double[L_new]; //xの重心を格納する配列
	    double[] cen_gra_y = new double[L_new]; //yの重心を格納する配列
	    for(int i = 1; i < L_new; i++){ //座標の合計を計算
		for(int y=0; y<ysize; y++) {
		    for(int x=0; x<xsize; x++){
			int value = labelImg.getUnsignedValue(x, y);
			if(value == i){
			    sum_x[i] += x;
			    sum_y[i] += y;
			}
		    }
		}
	    }
	    for(int i = 1; i < L_new; i++){ //重心を計算
		if(area[i] != 0){
		    cen_gra_x[i] = (double)(sum_x[i]) / (double)area[i];
		    cen_gra_y[i] = (double)(sum_y[i]) / (double)area[i];
		}
	    }

	    int num = 1; //図形の番号

	    for(int i = 1; i < L_new; i++){ //特徴量を出力
		if(area[i] > 1000){
		    System.out.println("面積 " + num + " = " + area[i]);
		    System.out.println("周囲長 " + num + " = " + peri[i]);
		    System.out.println("円形度 " + num + " = " + circle[i]);
		    if(0.75 < circle[i] && circle[i] < 1.3){
			System.out.println("これは円です");
		    }
		    System.out.println("重心 " + num + " = " + "(" + cen_gra_x[i] + ", " + cen_gra_y[i] + ")");
		    System.out.println("");
		    num++;
		}
	    }

	    // 出力用の画像を書き出す.
	    labelImg.writeVoxels(args[1]);
	}

	catch(Exception e) {
	    e.printStackTrace();
	}
    }
}
