package cn.nodemedia.library.glide;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Glide 普通图片转换器 Created by Bining.
 */
public class GlideNormalTransform extends BitmapTransformation {

	public GlideNormalTransform(Context context) {
		super(context);
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return toTransform;
	}

	@Override
	public String getId() {
		return getClass().getName();
	}
}
