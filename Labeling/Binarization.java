import java.io.*;
import hpkg.fund.pnm.*;

public class Binarization
{
    public static void main(String[] args) {
	try {
	    double t_Max = 0; //しきい値の最大
	    double sb2; //クラス間分散
	    double sb2_Max = 0; //クラス間分散の最大（初期値　０）
	    double n1 = 0; //1-画素の総数
	    double sum1 = 0; //1-画素の画素値和
	    double n0 = 0; //0-画素の総数
	    double sum0 = 0; //0-画素の画素値和
	    double myu1 = 0; //1-領域の濃淡地の平均
	    double myu0 = 0; //0-領域の濃淡地の平均


	    // コマンドライン引数を解析する.

	    if(args.length != 2)
	    {
		System.err.println("java 2_a 入力画像.pgm 出力画像.pgm");
		System.exit(0);
	    }

	    HPnm inImg = new HPnm();
	    inImg.readVoxels(args[0]);
	    // 入力した画像のサイズ xsize (横) と ysize (縦) を取得する.
	    int ysize = inImg.ysize();
	    int xsize = inImg.xsize();
	    // 出力用の画像を生成する.画像サイズは(xsize,ysize)で，
	    // 画素サイズは 8 Bit Per Pixel.
	    HPnm outImg = new HPnm(xsize, ysize, 8);

	    for(int t = 0; t <= 255; t++) { //しきい値tが1から255まで
		// y を 0 から ysize-1 まで，x を 0 から xsize-1 までそれぞれ
		// 1ずつ変化させて，以下の処理を繰り返す.なお，この順番に画素に
		// アクセスすることを「ラスタスキャン」という.
		for(int y=0; y<ysize; y++) {
		    for(int x=0; x<xsize; x++) {
			// 入力画像の位置 (x,y) における画素値 (符号なし) を取得する.
			int value = inImg.getUnsignedValue(x, y);
			// その画素値が「しきい値」以上のときだけ，出力画像の同じ位置の
			// 画素に1をセットする(outImg の画素値のデフォルトは0である
			// ことに注意する).
			if(value >= t){
			    n1++; //1-画素の総数を１増やす
			    sum1 += value; //1-画素の画素値和を増やす
			} else {
			    n0++; //0-画素の総数を１増やす
			    sum0 += value; //0-画素の画素値和を増やす
			}

		    }
		}

		if (n1 != 0) {
		    myu1 = sum1 / n1; //濃淡値の平均を計算
		}
		if (n0 != 0) {
		    myu0 = sum0 / n0;
		}

		sb2 = (n0 / (n0 + n1)) * (n1 / (n0 + n1)) * (myu0 - myu1)*(myu0 - myu1); //クラス間分散を計算

		if (sb2 >= sb2_Max) { //最大値を書き換える
		    t_Max = t;
		    sb2_Max = sb2;
		}

		//System.out.println(sb2);
		//System.out.println(myu0);

		n1 = 0;
		n0 = 0;
		sum1 = 0;
		sum0 = 0;
	    }

		//もとまった値で２値化を行う

		double threshold = t_Max; //しきい値を最大値に設定

		// y を 0 から ysize-1 まで，x を 0 から xsize-1 までそれぞれ
		// 1ずつ変化させて，以下の処理を繰り返す.なお，この順番に画素に // アクセスすることを「ラスタスキャン」という.
		for(int y=0; y<ysize; y++){
		    for(int x=0; x<xsize; x++){

			// 入力画像の位置 (x,y) における画素値 (符号なし) を取得する.
			int value = inImg.getUnsignedValue(x, y);

			// その画素値が「しきい値」以上のときだけ，出力画像の同じ位置の
			// 画素に1をセットする(outImg の画素値のデフォルトは0である
			// ことに注意する).

			/*if(value >= threshold){
				outImg.setValue(x, y, 255); // <== 8 bit の最大値にする.
				}*/

			if(value >= threshold){
			    outImg.setValue(x, y, 0);
			} else {
			    outImg.setValue(x, y, 255);
			}
		    }
		}
		// 出力用の画像を書き出す.
		System.out.println("しきい値 = " + t_Max);
		outImg.writeVoxels(args[1]);
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
}
