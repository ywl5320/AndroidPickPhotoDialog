package com.ywl5320.csdn.pickphoto.beans;

/**
 * Created by ywl on 2016/11/22.
 */

public class ImgBean {

    private String path;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
