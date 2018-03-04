package com.kuaishou.hb.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by cs on 2017/5/25.
 */

public class BitmapUtils {

	/**
	 * 创建右下角图片水印
	 *
	 * @param src
	 * @param watermark 水印图片
	 * @return
	 */
	public static Bitmap createBitmap(Bitmap src, Bitmap watermark) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		//create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);//创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		//draw src into
		cv.drawBitmap(src, 0, 0, null);//在 0，0坐标开始画入src
		//draw watermark into
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);//在src的右下角画入水印
		//save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);//保存
		//store
		cv.restore();//存储

		return newb;
	}
}
