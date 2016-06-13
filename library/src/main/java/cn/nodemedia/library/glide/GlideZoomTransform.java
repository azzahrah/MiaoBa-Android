package cn.nodemedia.library.glide;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Glide 图片缩放转换器 Created by Bining.
 */
public class GlideZoomTransform extends BitmapTransformation {

	private int maxWidth;

	public GlideZoomTransform(Context context, int maxWidth) {
		super(context);
		this.maxWidth = maxWidth;
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return zoomBitmap(toTransform);
	}

	private Bitmap zoomBitmap(Bitmap source) {
		if (maxWidth > 0 && source != null) {
			int bWidth = source.getWidth();
			int bHeight = source.getHeight();

			float scale = 0;
			if (bWidth > maxWidth) {
				scale = (float) maxWidth / (float) bWidth;
			} else if (bHeight > maxWidth * 3) {
				scale = (float) maxWidth * 3 / (float) bHeight;
			}
			if (scale > 0) {
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				return Bitmap.createBitmap(source, 0, 0, bWidth, bHeight, matrix, true);
			}
		}
		return source;
	}

	@Override
	public String getId() {
		return getClass().getName();
	}
}
