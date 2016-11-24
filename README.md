# AndroidPickPhotoDialog
###Android图片选择对话框，通过本地相册或照相机获得图片，可单选或多选，单选可设置是否裁剪<br/>
####1张图片
![image](https://github.com/wanliyang1990/AndroidPickPhotoDialog/blob/master/imgs/1.png)<br/>
####2张图片
![image](https://github.com/wanliyang1990/AndroidPickPhotoDialog/blob/master/imgs/2.png)<br/>
####3张图片
![image](https://github.com/wanliyang1990/AndroidPickPhotoDialog/blob/master/imgs/3.png)<br/>
####4张图片
![image](https://github.com/wanliyang1990/AndroidPickPhotoDialog/blob/master/imgs/4.png)<br/>

调用方法：<br/>

        private PickPhotoDialog pickPhotoDialog;
        
        //点击事件里面添加
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
        
        //权限申请
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
        

    
create by ywl5320