package com.cocoon.jay.printerwebcontent.printer.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


public class PrinterUtils {

    private Context mContext;

    public final static int WIDTH_PIXEL = 384;
    public final static int IMAGE_SIZE = 320;


    /**
     * 复位打印机
     */
    public static final byte[] RESET = {0x1b, 0x40};

    /**
     * 检查是否有纸指令
     */
    public static final byte[] CHECK_PAPER = new byte[]{0x10, 0x04, 0x04};

    /**
     * 切纸并且走纸
     */
    public static final byte[] CUT = new byte[]{0x1b, 0x69};

    /**
     * 切纸并且走纸
     */
    public static final byte[] zouzhi = new byte[]{0x1d, 0x56, 0x42, 0x00};

    /**
     * 左对齐
     */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式
     */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍
     */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍
     */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 字体不放大
     */
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};
    /**
     * 字体设置中号
     */
    public static final byte[] MIDDLE = {0x1d, 0x21, 0x02};
    /**
     * 字体设置大号(宽高加倍)
     */
    public static final byte[] LARGE = {0x1d, 0x21, 0x11};

    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};
    /**
     * 打印纸一行最大的字节 58mm
     */
    private static final int LINE_BYTE_SIZE = 32;

    /**
     * 打印纸一行最大的字节 80mm
     */
    private static final int LINE_BYTE_SIZE_80 = 48;

    /**
     * 打印三列时，中间一列的中心线距离打印纸左侧的距离
     */
    private static final int LEFT_LENGTH = 18;
    /**
     * 打印三列时，中间一列的中心线距离打印纸右侧的距离
     */
    private static final int RIGHT_LENGTH = 14;
    /**
     * 打印三列时，中间一列的中心线距离打印纸左侧的距离
     */
    private static final int LEFT_LENGTH_80 = 28;
    /**
     * 打印三列时，中间一列的中心线距离打印纸右侧的距离
     */
    private static final int RIGHT_LENGTH_80 = 20;


    /**
     * 打印三列时，第一列汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 7;


    private static final byte ESC = 27;//换码
    /**
     * 打印机尺寸,默认58mm
     */
    private static int printerSize = 0;


    public void setContext(Context context) {
        mContext = context;
    }

    private OutputStream outputStream;

    public boolean isconnected() {
        return isconnected;
    }

    public void setIsconnected(boolean isconnected) {
        this.isconnected = isconnected;
    }

    private boolean isconnected;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * 绘制下划线（1点宽）
     *
     * @return
     */
    public static byte[] underlineWithOneDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 1;
        return result;
    }

    /**
     * 进纸并全部切割
     *
     * @throws IOException
     */
    public static void feedAndCut(OutputStream outputStream) {
        try {
            outputStream.write(0x1D);
            outputStream.write(86);
            outputStream.write(65);
            //切纸前走纸多少
            outputStream.write(85);
            outputStream.flush();
            //另外一种切纸的方式
            byte[] bytes = {29, 86, 0};
            outputStream.write(bytes);
        } catch (Exception e) {
            Log.e("", "feedAndCut" + e);
        }
    }

    /**
     * 切纸命令
     */
    public static byte[] getCutPaperByte() {
        byte[] buffer = new byte[5];
        buffer[0] = '\n';//命令必须是单行
        buffer[1] = 29;
        buffer[2] = 86;
        buffer[3] = 66;
        buffer[4] = 1;
        return buffer;
    }

    public int doCheckPaperState(Socket socket) {
        /**1：正常，0：异常，-1：链接失败*/
        int flag = 0;
        try {
            InputStream bis = socket.getInputStream();
//            outputStream.write(CHECK_PAPER);
//            outputStream.flush();
            int tmp = bis.read();
            if (tmp == 18) {
                flag = 1;
            } else {
                flag = 0;
            }
        } catch (Exception e) {
            flag = -1;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 绘制下划线（2点宽）
     *
     * @return
     */
    public static byte[] underlineWithTwoDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 2;
        return result;
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    @SuppressLint("NewApi")
    private static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }

    /**
     * 水平方向向右移动col列
     *
     * @param col
     * @return
     */
    public static byte[] set_HT_position(byte col) {
        byte[] result = new byte[4];
        result[0] = ESC;
        result[1] = 68;
        result[2] = col;
        result[3] = 0;
        return result;
    }

    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String printTwoData(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);
        int marginBetweenMiddleAndRight;
        // 计算两侧文字中间的空格
        if (printerSize == 0) {
            marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;
        } else {
            marginBetweenMiddleAndRight = LINE_BYTE_SIZE_80 - leftTextLength - rightTextLength;
        }
        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString();
    }


    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String printThreeData(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
        }
        int leftTextLength = getBytesLength(leftText);
        int middleTextLength = getBytesLength(middleText);
        int rightTextLength = getBytesLength(rightText);

        sb.append(leftText);
        int marginBetweenLeftAndMiddle;
        // 计算两侧文字中间的空格
        if (printerSize == 0) {
            marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;
        } else {
            marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;
        }
        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);
        Log.e("", "printThreeData sb left " + sb.toString());
        int marginBetweenMiddleAndRight;
        if (printerSize == 0) {
            // 计算右侧文字和中间文字的空格长度
            marginBetweenMiddleAndRight = (RIGHT_LENGTH - middleTextLength / 2 - rightTextLength) + 1;
        } else {
            marginBetweenMiddleAndRight = RIGHT_LENGTH_80 - middleTextLength / 2 - rightTextLength;
        }
        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        Log.e("", "printThreeData sb right " + sb.toString());
        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        Log.e("", "printThreeData sb right =" + sb.toString());
        return sb.toString();
    }


    /**
     * 设置打印格式
     *
     * @param command 格式指令
     */
    public static void selectCommand(OutputStream outputStream, byte[] command) {
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            Log.e("", "selectCommand " + e);
        }
    }

    /**
     * 设置打印格式
     *
     * @param command 格式指令
     */
    public void selectCommand(byte[] command) {
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {

        }
    }


    public String getLine58() {
        return "--------------------------------";
    }

    public String getLine80() {
        return "------------------------------------------------";
    }

    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public void printText(String text) {
        try {
            if (outputStream == null) {
                Toast.makeText(mContext, "请先连接上打印机", Toast.LENGTH_SHORT).show();
                return;
            }
            selectCommand(outputStream, PrinterUtils.RESET);
            selectCommand(outputStream, PrinterUtils.LINE_SPACING_DEFAULT);
            selectCommand(outputStream, PrinterUtils.ALIGN_LEFT);
            //默认使用小号
            selectCommand(outputStream, PrinterUtils.NORMAL);
            byte[] data = text.getBytes("GB2312");
            Log.d("TAG", "DATA====="+data);
            Log.d("TAG", "DATA====="+data.length);
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            Log.e("", "printText " + e);
        }
    }

    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public void printText(OutputStream outputStream, String text) {
        try {
            selectCommand(outputStream, PrinterUtils.RESET);
            selectCommand(outputStream, PrinterUtils.LINE_SPACING_DEFAULT);
            selectCommand(outputStream, PrinterUtils.ALIGN_LEFT);
            //默认使用小号
            selectCommand(outputStream, PrinterUtils.NORMAL);
            byte[] data = text.getBytes("GB2312");
            Log.d("TAG", "DATA====="+data);
            Log.d("TAG", "DATA====="+data.length);
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            Log.e("", "printText " + e);
        }
    }


    public int getPrinterSize() {
        return printerSize;
    }

    public void setPrinterSize(int Size) {
        printerSize = Size;
    }


    //--------------------------new add----------------------------------------------------

    /**
     * 打印bitmap
     * @param bitmap
     */

    public void print(Bitmap bitmap) {

        try {
            if (outputStream == null) {
                Toast.makeText(mContext, "请先连接上打印机", Toast.LENGTH_SHORT).show();
                return;
            }
            selectCommand(outputStream, PrinterUtils.RESET);
            selectCommand(outputStream, PrinterUtils.LINE_SPACING_DEFAULT);
            selectCommand(outputStream, PrinterUtils.ALIGN_LEFT);
            //默认使用小号
            selectCommand(outputStream, PrinterUtils.NORMAL);

            printBitmap(bitmap);

        } catch (IOException e) {
//            ToastUtil.Toast("请检测打印设备");
            Log.e("", "printText " + e);
        }


    }


    public void printBitmap(Bitmap bmp) throws IOException {
        bmp = compressPic(bmp);
        byte[] bmpByteArray = draw2PxPoint(bmp);
        outputStream.write(bmpByteArray);
        outputStream.flush();
    }


    /*************************************************************************
     * 假设一个360*360的图片，分辨率设为24, 共分15行打印 每一行,是一个 360 * 24 的点阵,y轴有24个点,存储在3个byte里面。
     * 即每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     **************************************************************************/
    private byte[] draw2PxPoint(Bitmap bmp) {
        //先设置一个足够大的size，最后在用数组拷贝复制到一个精确大小的byte数组中
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] tmp = new byte[size];
        int k = 0;
        // 设置行距为0
        tmp[k++] = 0x1B;
        tmp[k++] = 0x33;
        tmp[k++] = 0x00;
        // 居中打印
        tmp[k++] = 0x1B;
        tmp[k++] = 0x61;
        tmp[k++] = 1;
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            tmp[k++] = 0x1B;
            tmp[k++] = 0x2A;// 0x1B 2A 表示图片打印指令
            tmp[k++] = 33; // m=33时，选择24点密度打印
            tmp[k++] = (byte) (bmp.getWidth() % 256); // nL
            tmp[k++] = (byte) (bmp.getWidth() / 256); // nH
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int m = 0; m < 3; m++) {
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        tmp[k] += tmp[k] + b;
                    }
                    k++;
                }
            }
            tmp[k++] = 10;// 换行
        }
        // 恢复默认行距
        tmp[k++] = 0x1B;
        tmp[k++] = 0x32;

        byte[] result = new byte[k];
        System.arraycopy(tmp, 0, result, 0, k);
        return result;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    private byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }

    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param bitmapOrg
     */
    private Bitmap compressPic(Bitmap bitmapOrg) {
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = IMAGE_SIZE;
        int newHeight = IMAGE_SIZE;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmapOrg, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }



    /**
     * 将网页内容转成Bitmap
     *
     * @param webView
     */
    public static Bitmap saveImage(WebView webView) {
        Bitmap b = null;
        try {

//            webView.setDrawingCacheEnabled(true);
//            b = webView.getDrawingCache();

            Picture picture = webView.capturePicture();
            b = Bitmap.createBitmap(
                    picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            picture.draw(c);
            File file = new File("/sdcard/" + "contract.jpg");
            if(file.exists()){
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            if (fos != null) {
                b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


    public static Bitmap captureWebViewLong(WebView webView){
        Bitmap bitmap = null;
        try {
//            float scale = webView.getScale();
//            int webViewHeight = (int) (webView.getContentHeight()*scale+0.5);
//            bitmap = Bitmap.createBitmap(webView.getWidth(),webViewHeight, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            webView.draw(canvas);
//            File file = new File("/sdcard/" + "contract.jpg");
//            if(file.exists()){
//                file.delete();
//            }
//            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
//            if (fos != null) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.close();
//            }


            // WebView 生成长图，也就是超过一屏的图片，
            webView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());
            webView.setDrawingCacheEnabled(true);
            webView.buildDrawingCache();
            bitmap = Bitmap.createBitmap(webView.getMeasuredWidth(),
                    webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);  // 画布的宽高和 WebView 的网页保持一致
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, 0, webView.getMeasuredHeight(), paint);
            webView.draw(canvas);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}
