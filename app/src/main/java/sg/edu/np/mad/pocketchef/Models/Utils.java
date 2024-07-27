package sg.edu.np.mad.pocketchef.Models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Uri createImageContentUri(Bitmap bitmap,Context context) {
        ContentResolver resolver = context.getContentResolver();

        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap is null.");
        }

        // 创建一个新的图像文件
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "shared_image_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            throw new RuntimeException("Failed to insert new image into MediaStore.");
        }

        try (OutputStream outputStream = resolver.openOutputStream(uri)) {
            if (outputStream != null) {
                boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                if (!result) {
                    throw new RuntimeException("Failed to compress bitmap to JPEG.");
                }
                outputStream.flush(); // 确保数据写入
            } else {
                throw new RuntimeException("Failed to open output stream for the image.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing image to file.", e);
        }
        return uri;
    }

    public static String getLocalTime() {
        // 获取当前的本地日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 设定日期时间的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 对当前日期和时间进行格式化
       return now.format(formatter);
    }

    public static Bitmap Create2DCode(String str) {
        try {
            Logger.d("str == "+str);
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.QR_VERSION,10); // 指定二维码的版本为10
            BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 0, 0, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            //  二维矩阵转为一维像素数组（一直横着排）
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000; // 黑色
                    } else {
                        pixels[y * width + x] = 0xffffffff; // 白色背景
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap, 具体参考api
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] uriToBytes(Context context, Uri uri) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.e("Utils", "Error reading Uri to bytes", e);
            return null;
        }
    }
}
