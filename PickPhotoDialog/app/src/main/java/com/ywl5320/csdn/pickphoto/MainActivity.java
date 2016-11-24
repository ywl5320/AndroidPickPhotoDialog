package com.ywl5320.csdn.pickphoto;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ywl5320.csdn.pickphoto.adviewpager.adutils.AdViewpagerUtil;
import com.ywl5320.csdn.pickphoto.beans.ImgBean;
import com.ywl5320.csdn.pickphoto.dialog.PickPhotoDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl on 2016/11/22.
 */

public class MainActivity extends AppCompatActivity {

    private Button btnChoicePhoto;
    private ViewPager viewpager;
    private LinearLayout lydots;
    private AdViewpagerUtil adViewpagerUtil;

    private PickPhotoDialog pickPhotoDialog;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnChoicePhoto = (Button) findViewById(R.id.btn_choice_photo);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        lydots = (LinearLayout) findViewById(R.id.ly_dots);

        btnChoicePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adViewpagerUtil != null) {
                    adViewpagerUtil.stopLoopViewPager();
                }
                pickPhotoDialog = new PickPhotoDialog(MainActivity.this, MainActivity.this);
                Window window = pickPhotoDialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.DialogEnter);
                pickPhotoDialog.setCutImg(true, 5);
                pickPhotoDialog.setOnPhotoResultListener(new PickPhotoDialog.OnPhotoResultListener() {
                    @Override
                    public void onCameraResult(String path) {//相机拍照图片路径
                        List<ImgBean> imgBeens = new ArrayList<ImgBean>();
                        ImgBean imgBean = new ImgBean();
                        imgBean.setPath(path);
                        imgBeens.add(imgBean);
                        adViewpagerUtil = new AdViewpagerUtil(MainActivity.this, viewpager, lydots, 8, 4, imgBeens);
                        adViewpagerUtil.initVps();
                    }

                    @Override
                    public void onCutPhotoResult(Bitmap bitmap) {
                        //图片(相机和相册)裁剪后返回的bitmap
                    }

                    @Override
                    public void onPhotoResult(List<ImgBean> selectedImgs) {//相册多图选择返回图片路径结果集
                        if(selectedImgs != null && selectedImgs.size() > 0) {
                            adViewpagerUtil = new AdViewpagerUtil(MainActivity.this, viewpager, lydots, 8, 4, selectedImgs);
                            adViewpagerUtil.initVps();
                        }
                        else
                        {
                            if(adViewpagerUtil != null) {
                                adViewpagerUtil.startLoopViewPager();
                            }
                        }
                    }
                });
                pickPhotoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(adViewpagerUtil != null) {
                            adViewpagerUtil.startLoopViewPager();
                        }
                    }
                });

                pickPhotoDialog.show();
            }
        });
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (pickPhotoDialog != null)
        {
            pickPhotoDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(pickPhotoDialog != null)
        {
            pickPhotoDialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
