package com.ywl5320.csdn.pickphoto.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.ywl5320.csdn.pickphoto.R;
import com.ywl5320.csdn.pickphoto.beans.ImgBean;
import com.ywl5320.csdn.pickphoto.util.AppUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ywl on 2016/6/14.
 */
public class PickPhotoDialog extends BaseDialog{

    private OnPhotoResultListener onPhotoResultListener;
    private static final int REQUEST_CAMERA_CODE = 0x0002;
    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 0x0003;
    private static final int REQUEST_CAMERA_RESULT_CODE = 0x0004;
    private static final int REQUEST_CLICK_PHOTO_CODE = 0x0005;

    private Activity activity;
    private File file;
    private Uri imageUri;
    private String imgname = "";

    private boolean isCutImg = false;
    private int maxcount = 1;

    private Button btnCamera;
    private Button btnPhoto;

    private List<ImgBean> selectedImgs;

    public void setSelectedImgs(List<ImgBean> selectedImgs) {
        this.selectedImgs = selectedImgs;
    }

    public PickPhotoDialog(Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    public void setCutImg(boolean cutImg, int maxcount) {
        isCutImg = cutImg;
        this.maxcount = maxcount;
        if(isCutImg)
        {
            this.maxcount = 1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice_photo_layout);
        btnCamera = (Button) findViewById(R.id.btn_takephoto);
        btnPhoto = (Button) findViewById(R.id.btn_photo);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    Toast.makeText(context, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED//相机权限
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED//读取存储卡权限
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CODE);
                }
                else
                {
                    file = new File(getImgPath());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    imgname = getHeadImgName();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                        imageUri = Uri.fromFile(new File(file, imgname));
                    }else{
                        //7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider 并且这样可以解决MIUI系统上拍照返回size为0的情况
                        imageUri = FileProvider.getUriForFile(context, AppUtil.getAppPackName(context) + ".provider", new File(file, imgname));
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    activity.startActivityForResult(intent, REQUEST_CAMERA_RESULT_CODE);
                }
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    Toast.makeText(context, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CODE);
                }
                else
                {
                    PhotoListDialog photoListDialog = new PhotoListDialog(context, activity);
                    photoListDialog.setMAX_COUNT(maxcount);
                    photoListDialog.setAlreadySelectedImgs(selectedImgs);
                    photoListDialog.show();
                    photoListDialog.setOnChoicePhotoListener(new PhotoListDialog.OnChoicePhotoListener() {
                        @Override
                        public void onResult(List<ImgBean> imgBeens) {
                            if(isCutImg && imgBeens != null && imgBeens.size() > 0)
                            {
                                try {
                                    Uri photouri = Uri.fromFile(new File(imgBeens.get(0).getPath()));
                                    cropPhoto(photouri);
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                if (onPhotoResultListener != null) {
                                    onPhotoResultListener.onPhotoResult(imgBeens);
                                }
                                dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void setOnPhotoResultListener(OnPhotoResultListener onPhotoResultListener) {
        this.onPhotoResultListener = onPhotoResultListener;
    }

    public interface OnPhotoResultListener
    {
        void onCameraResult(String path);

        void onCutPhotoResult(Bitmap bitmap);

        void onPhotoResult(List<ImgBean> selectedImgs);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == REQUEST_CAMERA_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                file = new File(getImgPath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                imgname = getHeadImgName();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    imageUri = Uri.fromFile(new File(file, imgname));
                }else{
                    //7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider 并且这样可以解决MIUI系统上拍照返回size为0的情况
                    imageUri = FileProvider.getUriForFile(context, AppUtil.getAppPackName(context) + ".provider", new File(file, imgname));
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(intent, REQUEST_CAMERA_RESULT_CODE);
            } else
            {
                Toast.makeText(context, "请允许打开摄像头权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        else if(requestCode == REQUEST_EXTERNAL_STORAGE_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                PhotoListDialog photoListDialog = new PhotoListDialog(context, activity);
                photoListDialog.setMAX_COUNT(9);
                photoListDialog.show();
            } else
            {
                Toast.makeText(context, "请允许读取存储卡权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_CAMERA_RESULT_CODE)
        {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        imageUri.getPath(), imgname, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));

            if(!isCutImg) {
                if (onPhotoResultListener != null) {
                    onPhotoResultListener.onCameraResult(imageUri.getPath());
                }
                dismiss();
            }
            else
            {
                cropPhoto(imageUri);
            }
        }
        else if(requestCode == REQUEST_CLICK_PHOTO_CODE)
        {
            Bitmap photo = data.getParcelableExtra("data");
            if(photo!= null){
                if (onPhotoResultListener != null) {
                    onPhotoResultListener.onCutPhotoResult(photo);
                }
            }
            dismiss();
        }
    }

    public void cropPhoto(Uri uri)
    {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            intent.setDataAndType(uri, "image/*");
        }else{
            intent.setDataAndType(getImageContentUri(new File(uri.getPath())), "image/*");// mUri是已经选择的图片Uri
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);// 输出图片大小
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, REQUEST_CLICK_PHOTO_CODE);
    }

    public String getImgPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/bdgames/images/";
    }

    public String getHeadImgName()
    {
        return System.currentTimeMillis() + ".jpg";
    }

    /**
     * 转换 content:// uri
     *
     * @param imageFile
     * @return
     */
    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
