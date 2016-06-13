package cn.nodemedia.library.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.nodemedia.library.qrcode.encode.QRCodeWriter;
import cn.nodemedia.library.qrcode.encode.common.BitMatrix;
import cn.nodemedia.library.qrcode.encode.ex.WriterException;
import cn.nodemedia.library.qrcode.encode.utils.EncodeHintType;
import cn.nodemedia.library.qrcode.encode.utils.ErrorCorrectionLevel;
import cn.nodemedia.library.utils.ImageUtils;
import cn.nodemedia.library.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成二维码
 * Created by Bining.
 */
public class QRCodeUtils {

    public static Bitmap createQRImageToCommon(Context context, String content) {
        return createQRImage(context, content, getDefSize(), 0);
    }

    public static Bitmap createQRImageToSize(Context context, String content, int qrcodeSize) {
        return createQRImage(context, content, qrcodeSize, 0);
    }

    public static Bitmap createQRImageToLogo(Context context, String content, int logoRes) {
        return createQRImage(context, content, getDefSize(), logoRes);
    }

    public static Bitmap createQRImage(Context context, String content, int qrcodeSize, int logoRes) {
        return createQRImage(content, qrcodeSize, getLogoPixels(context, logoRes, qrcodeSize));
    }

    public static Bitmap createQRImage(String content, int qrcodeSize, int[][] logoPixels) {
        if (content != null && !"".equals(content) && content.length() > 0) {
            try {
                BitMatrix e = (new QRCodeWriter()).encode(content, qrcodeSize, qrcodeSize, getDecodeHintType());
                int[] pixels = new int[qrcodeSize * qrcodeSize];
                int bitmap;
                int x;
                if (logoPixels != null) {
                    bitmap = qrcodeSize / 2;
                    x = logoPixels.length / 2;
                    int frameWidth = x / 35;

                    for (int y = 0; y < qrcodeSize; ++y) {
                        for (int x1 = 0; x1 < qrcodeSize; ++x1) {
                            if (x1 > bitmap - x && x1 < bitmap + x && y > bitmap - x && y < bitmap + x) {
                                pixels[y * qrcodeSize + x1] = logoPixels[x1 - bitmap + x][y - bitmap + x];
                            } else if (x1 > bitmap - x - frameWidth && x1 < bitmap - x + frameWidth && y > bitmap - x - frameWidth && y < bitmap + x + frameWidth || x1 > bitmap + x - frameWidth && x1 < bitmap + x + frameWidth && y > bitmap - x - frameWidth && y < bitmap + x + frameWidth || x1 > bitmap - x - frameWidth && x1 < bitmap + x + frameWidth && y > bitmap - x - frameWidth && y < bitmap - x + frameWidth || x1 > bitmap - x - frameWidth && x1 < bitmap + x + frameWidth && y > bitmap + x - frameWidth && y < bitmap + x + frameWidth) {
                                pixels[y * qrcodeSize + x1] = 268435455;
                            } else {
                                pixels[y * qrcodeSize + x1] = e.get(x1, y) ? -16777216 : 268435455;
                            }
                        }
                    }
                } else {
                    for (bitmap = 0; bitmap < qrcodeSize; ++bitmap) {
                        for (x = 0; x < qrcodeSize; ++x) {
                            pixels[bitmap * qrcodeSize + x] = e.get(x, bitmap) ? -16777216 : 268435455;
                        }
                    }
                }

                Bitmap var11 = Bitmap.createBitmap(qrcodeSize, qrcodeSize, Bitmap.Config.ARGB_8888);
                var11.setPixels(pixels, 0, qrcodeSize, 0, 0, qrcodeSize, qrcodeSize);
                return var11;
            } catch (WriterException var10) {
                var10.printStackTrace();
            }
        }

        return null;
    }

    private static Map<EncodeHintType, Object> getDecodeHintType() {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        return hints;
    }

    public static int getDefSize() {
        return (int) ((double) ScreenUtils.getScreenHeight() * 0.7D);
    }

    public static int[][] getLogoPixels(Context context, int logeRes, int qrcodeSize) {
        if (logeRes <= 0) {
            return null;
        } else {
            int logoSize = qrcodeSize / 4;
            Bitmap bitmap = ImageUtils.zoomBitmap(BitmapFactory.decodeResource(context.getResources(), logeRes), logoSize, logoSize);
            int[][] logoPixels = new int[bitmap.getWidth()][bitmap.getHeight()];

            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    logoPixels[i][j] = bitmap.getPixel(i, j);
                }
            }

            return logoPixels;
        }
    }
}
