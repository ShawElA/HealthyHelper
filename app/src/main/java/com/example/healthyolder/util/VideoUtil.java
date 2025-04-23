package com.example.healthyolder.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoUtil {

    /**
     * 获取本地视频的第一帧
     * @param localPath
     * @return
     */
    public static Bitmap GetLocalVideoBitmap(String localPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    /**
     * 把bitmap 转file
     * @param bitmap
     */
    public static String SaveBitmapFile(Context context, Bitmap bitmap, String fileName){
        String filePath = "";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //android 11
//            filePath = Config.SDCardConstants.getDir(context) + File.separator + "TravelNotes" + File.separator;
//        }else {
            filePath = "/mnt/sdcard/CourseHomework/";
//        }
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file=new File(filePath, fileName);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.showBottomToast(e.getMessage() + " " + e.getCause());
        }
        return filePath + fileName;
    }

    //从uri中获取真实路径
    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
}
