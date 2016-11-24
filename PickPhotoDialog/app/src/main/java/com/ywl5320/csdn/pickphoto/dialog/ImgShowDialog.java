package com.ywl5320.csdn.pickphoto.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ywl5320.csdn.pickphoto.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ywl on 2016/11/23.
 */

public class ImgShowDialog extends BaseDialog{

    private PhotoView photoView;
    private String imgpath;
    private PhotoViewAttacher mAttacher;

    public ImgShowDialog(Context context) {
        super(context);
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_photo_show_layout);
        photoView = (PhotoView) findViewById(R.id.photoview);
        Glide.with(context).load(imgpath).into(photoView);
        mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {

            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }
}
