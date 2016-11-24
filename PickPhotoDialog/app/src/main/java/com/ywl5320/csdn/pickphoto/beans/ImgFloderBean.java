package com.ywl5320.csdn.pickphoto.beans;

/**
 * Created by ywl on 2016/11/21.
 */

public class ImgFloderBean {

    private String dir;
    private String firstImgPath;
    private String dirName;
    private int size;
    private boolean isSelected;
    private int type;//0全部 非0文件夹

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }
}
