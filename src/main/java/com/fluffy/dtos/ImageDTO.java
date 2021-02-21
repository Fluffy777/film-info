package com.fluffy.dtos;

public class ImageDTO {
    private byte[] data;
    private int width;
    private int height;
    private int pictureType;
    private String filename;

    public ImageDTO() {

    }

    public ImageDTO(byte[] data, int width, int height, int pictureType, String filename) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.pictureType = pictureType;
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPictureType() {
        return pictureType;
    }

    public void setPictureType(int pictureType) {
        this.pictureType = pictureType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
