package com.ywl5320.csdn.pickphoto.dialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ywl5320.csdn.pickphoto.R;
import com.ywl5320.csdn.pickphoto.adapter.ImgGridViewAdapter;
import com.ywl5320.csdn.pickphoto.adapter.ImgListViewAdapter;
import com.ywl5320.csdn.pickphoto.beans.ImgBean;
import com.ywl5320.csdn.pickphoto.beans.ImgFloderBean;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ywl on 2016/11/23.
 */

public class PhotoListDialog extends BaseDialog{

    private ProgressDialog progressDialog;

    private LinearLayout lySystemBar;
    private ImageView mivBack;
    private TextView mtvMenu;
    private TextView mtvTitle;
    private TextView tvSelected;
    private RelativeLayout rlActions;
    private RelativeLayout rlSelected;
    private View vBg;
    private Activity activity;

    private GridView gridView;
    private ListView listView;

    private List<ImgFloderBean> imgFloderBeens;
    private List<ImgBean> imgs;
    private List<ImgBean> tempImgs;
    private List<ImgBean> selectedImgBeans;
    private List<ImgBean> alreadySelectedImgs;

    private ImgGridViewAdapter imgGridViewAdapter;
    private ImgListViewAdapter imgListViewAdapter;

    private int listviewHeight = 0;
    private boolean isShowListView = false;
    private int MAX_COUNT = 9;
    private String dirPath = "";

    private OnChoicePhotoListener onChoicePhotoListener;


    public PhotoListDialog(Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    public void setAlreadySelectedImgs(List<ImgBean> alreadySelectedImgs) {
        this.alreadySelectedImgs = alreadySelectedImgs;
    }

    public void setOnChoicePhotoListener(OnChoicePhotoListener onChoicePhotoListener) {
        this.onChoicePhotoListener = onChoicePhotoListener;
    }


    public void setMAX_COUNT(int MAX_COUNT) {
        this.MAX_COUNT = MAX_COUNT;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_photo_list_layout);

        mtvTitle = (TextView) findViewById(R.id.tv_title);
        mivBack = (ImageView) findViewById(R.id.iv_back);
        mtvMenu = (TextView) findViewById(R.id.tv_right);
        lySystemBar = (LinearLayout) findViewById(R.id.ly_system_bar);
        initSystembar(lySystemBar);


        mtvTitle.setText("图片选择");
        mivBack.setVisibility(View.VISIBLE);
        mivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        gridView = (GridView) findViewById(R.id.gridview);
        tvSelected = (TextView) findViewById(R.id.tv_selected);
        listView = (ListView) findViewById(R.id.listview);
        rlActions = (RelativeLayout) findViewById(R.id.rl_actions);
        rlSelected = (RelativeLayout) findViewById(R.id.rl_selected);
        vBg = findViewById(R.id.v_bg);

        listviewHeight = (int)(getScreenHeight(activity) * 0.70f);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listviewHeight);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lp.setMargins(0, 0, 0, dip2px(context, 50));
        listView.setLayoutParams(lp);

        rlSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowListView) {
                    hideFloor(listView);
                }
                else {
                    showFloor(listView);
                }
            }
        });

        mtvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgGridViewAdapter.isaddpath())
                {
                    for(ImgBean imgBean : selectedImgBeans)
                    {
                        imgBean.setPath(dirPath + "/" + imgBean.getPath());
                    }
                }

                if(onChoicePhotoListener != null)
                {
                    onChoicePhotoListener.onResult(selectedImgBeans);
                }

                dismiss();
            }
        });

        hideFloor(listView);

        setAdapter();
        progressDialog = ProgressDialog.show(context, null, "正在加载...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanImgs();
            }
        }, 500);

    }

    public void setAdapter()
    {
        imgs = new ArrayList<>();
        tempImgs = new ArrayList<>();
        selectedImgBeans = new ArrayList<>();
        imgFloderBeens = new ArrayList<>();
        imgGridViewAdapter = new ImgGridViewAdapter(context, imgs);
        imgListViewAdapter = new ImgListViewAdapter(context, imgFloderBeens);
        gridView.setAdapter(imgGridViewAdapter);
        listView.setAdapter(imgListViewAdapter);
        mtvMenu.setText("(" + selectedImgBeans.size() + "/" + MAX_COUNT + ")");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImgBean imgBean = (ImgBean) gridView.getItemAtPosition(i);
                String imgPath = "";
                if(imgGridViewAdapter.isaddpath())
                {
                    imgPath = imgGridViewAdapter.getPpath() + "/" + imgBean.getPath();
                }
                else {
                    imgPath = imgBean.getPath();
                }
                ImgShowDialog imgShowDialog = new ImgShowDialog(context);
                imgShowDialog.setImgpath(imgPath);
                imgShowDialog.show();

            }
        });

        imgGridViewAdapter.setOnImgSelectedListener(new ImgGridViewAdapter.OnImgSelectedListener() {
            @Override
            public void onSelected(ImgBean imgBean) {
                if(selectedImgBeans.size() < MAX_COUNT) {
                    imgBean.setSelected(!imgBean.isSelected());
                    if (imgBean.isSelected()) {
                        if (MAX_COUNT == 1) {
                            for (ImgBean imb : selectedImgBeans) {
                                imb.setSelected(false);
                            }
                            selectedImgBeans.clear();
                            selectedImgBeans.add(imgBean);
                        } else {
                            if (!selectedImgBeans.contains(imgBean)) {
                                selectedImgBeans.add(imgBean);
                            }
                        }
                    } else {
                        if (selectedImgBeans.contains(imgBean)) {
                            selectedImgBeans.remove(imgBean);
                        }
                    }
                    System.out.println("size:" + selectedImgBeans.size());
                    mtvMenu.setText("(" + selectedImgBeans.size() + "/" + MAX_COUNT + ")");
                    imgGridViewAdapter.notifyDataSetChanged();
                }
                else
                {
                    if(imgBean.isSelected())
                    {
                        imgBean.setSelected(false);
                        if (selectedImgBeans.contains(imgBean)) {
                            selectedImgBeans.remove(imgBean);
                        }
                    }
                    mtvMenu.setText("(" + selectedImgBeans.size() + "/" + MAX_COUNT + ")");
                    imgGridViewAdapter.notifyDataSetChanged();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImgFloderBean imgFloderBean = (ImgFloderBean) listView.getItemAtPosition(i);
                if(!imgFloderBean.isSelected()) {
                    if (imgFloderBean.getType() == 0) {
                        imgs.clear();
                        imgs.addAll(tempImgs);
                        imgGridViewAdapter.setIsaddpath(false);
                        imgGridViewAdapter.notifyDataSetInvalidated();
                    } else {
                        scanChildDirPaths(imgFloderBean.getDir());
                    }
                    setSelectedFolder(i);
                    tvSelected.setText(imgFloderBean.getDirName());
                    for(ImgBean imb : selectedImgBeans)
                    {
                        imb.setSelected(false);
                    }
                    selectedImgBeans.clear();
                    mtvMenu.setText("(" + selectedImgBeans.size() + "/" + MAX_COUNT + ")");
                }
                hideFloor(listView);
            }
        });
    }


    public void setSelectedFolder(int position)
    {
        int size = imgFloderBeens.size();
        for(int i = 0; i < size; i++)
        {
            if(i == position)
            {
                imgFloderBeens.get(i).setSelected(true);
            }
            else
            {
                imgFloderBeens.get(i).setSelected(false);
            }
        }
        imgListViewAdapter.notifyDataSetChanged();
    }

    public void scanChildDirPaths(String dirPath)
    {
        this.dirPath = dirPath;
        File file = new File(dirPath);
        String[]childFilePaths = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if(s.endsWith(".jpg") || s.endsWith(".jpeg") || s.endsWith(".png"))
                    return true;
                return false;
            }
        });

        if(childFilePaths != null)
        {
            imgGridViewAdapter.setIsaddpath(true);
            imgGridViewAdapter.setPpath(dirPath);
            imgs.clear();
            int length = childFilePaths.length;
            for(int i = 0; i < length; i++)
            {
                ImgBean imgBean = new ImgBean();
                imgBean.setPath(childFilePaths[i]);
                imgs.add(imgBean);
            }
            Collections.reverse(imgs);
            imgGridViewAdapter.notifyDataSetInvalidated();
        }

    }

    private void sortSelectedImgs(List<ImgBean> imgs)
    {
        if(alreadySelectedImgs != null)
        {
            selectedImgBeans.clear();
            for(ImgBean img : alreadySelectedImgs)
            {
                for(ImgBean imgBean : imgs)
                {
                    if(img.getPath().equals(imgBean.getPath()))
                    {
                        imgBean.setSelected(true);
                        selectedImgBeans.add(imgBean);
                    }
                }
            }
            mtvMenu.setText("(" + selectedImgBeans.size() + "/" + MAX_COUNT + ")");
        }
    }


    public void scanImgs()
    {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(context, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread()
        {
            @Override
            public void run() {
                Uri muri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = activity.getContentResolver();

                Cursor cursor = cr.query(muri, null, MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ? ", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                Set<String> dirPaths = new HashSet<String>();
                int count = cursor.getCount();
                for(int i = count - 1; i >= 0; i--) {
                    cursor.moveToPosition(i);
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if(i == count - 1)
                    {
                        ImgFloderBean imgflder = new ImgFloderBean();
                        imgflder.setFirstImgPath(path);
                        imgflder.setDirName("所有图片");
                        imgflder.setType(0);
                        imgflder.setSelected(true);
                        imgFloderBeens.add(imgflder);
                    }

                    ImgBean imgBean = new ImgBean();
                    imgBean.setPath(path);
                    imgBean.setSelected(false);
//                    checkSelectedImgBeans(imgBean);
                    imgs.add(imgBean);
                    tempImgs.add(imgBean);
                    File parentFile = new File(path).getParentFile();

                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImgFloderBean imgFloderBean = null;

                    if (dirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        dirPaths.add(dirPath);
                        imgFloderBean = new ImgFloderBean();
                        imgFloderBean.setDir(dirPath);
                        imgFloderBean.setFirstImgPath(path);
                        imgFloderBean.setDirName(parentFile.getName());
                        imgFloderBean.setType(1);
                        imgFloderBean.setSelected(false);
                        imgFloderBeens.add(imgFloderBean);
                    }

                    if (parentFile.list() == null)
                        continue;

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            if (s.endsWith(".jpg") || s.endsWith(".jpeg") || s.endsWith(".png"))
                                return true;
                            return false;
                        }
                    }).length;

                    if (imgFloderBean != null) {
                        imgFloderBean.setSize(picSize);
                    }

                }
                cursor.close();
                Message message = Message.obtain();
                message.what = 0x001;
                handler.sendMessage(message);
            }
        }.start();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x001)
            {
                sortSelectedImgs(imgs);
                imgGridViewAdapter.setIsaddpath(false);
                imgGridViewAdapter.notifyDataSetInvalidated();
            }
            progressDialog.dismiss();
        }
    };

    public void showFloor(final View v) {
        isShowListView = true;
        ValueAnimator animator;
        animator = ValueAnimator.ofFloat(listviewHeight, 0);
        animator.setTarget(v);
        animator.setDuration(200).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((Float) animation.getAnimatedValue());
            }
        });

        vBg.animate().alpha(1).setDuration(200);
        vBg.setVisibility(View.VISIBLE);
    }

    public void hideFloor(final View v)
    {
        isShowListView = false;
        ValueAnimator animator;
        animator = ValueAnimator.ofFloat(0, listviewHeight);
        animator.setTarget(v);
        animator.setDuration(200).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        vBg.animate().alpha(0).setDuration(200);
        vBg.setVisibility(View.GONE);

    }

    public void initSystembar(View lySystemBar)
    {
        if(true) {
            if (Build.VERSION.SDK_INT >= 19) {
                if (lySystemBar != null) {
                    lySystemBar.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lySystemBar.getLayoutParams();
                    lp.height = getStatusHeight(activity);
                    lySystemBar.requestLayout();
                }
            } else {
                if (lySystemBar != null) {
                    lySystemBar.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface OnChoicePhotoListener
    {
        void onResult(List<ImgBean> imgBeens);
    }

    public int getScreenHeight(Activity context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenHeight = dm.heightPixels;
        return  mScreenHeight;
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getStatusHeight(Activity activity)
    {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }
        return statusBarHeight;
    }
}
