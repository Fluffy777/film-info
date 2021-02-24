package com.fluffy.dtos;

/**
 * Клас, що надає можливість зберігати дані про зображення.
 * @author Сивоконь Вадим
 */
public class ImageDTO {
    /**
     * Байти, із якого складається зображення.
     */
    private byte[] data;

    /**
     * Ширина зображення (px).
     */
    private int width;

    /**
     * Висота зображення (px).
     */
    private int height;

    /**
     * Тип зображення.
     */
    private int pictureType;

    /**
     * Назва файлу.
     */
    private String filename;

    /**
     * Створює порожній DTO зображення.
     */
    public ImageDTO() {
    }

    /**
     * Створює DTO зображення.
     * @param data байти файлу зображення
     * @param width ширина зображення (px)
     * @param height висота зображення (px)
     * @param pictureType тип зображення
     * @param filename назва файлу
     */
    public ImageDTO(final byte[] data, final int width, final int height, final int pictureType, final String filename) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.pictureType = pictureType;
        this.filename = filename;
    }

    /**
     * Повертає байти, із яких складається зображення.
     * @return байти, із яких складається зображення
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Встановлює байти, із яких складається зображення.
     * @param data байти, із яких складається зображення
     */
    public void setData(final byte[] data) {
        this.data = data;
    }

    /**
     * Повертає ширину зображення (px).
     * @return ширина зображення (px)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Встановлює ширину зображення (px).
     * @param width ширина зображення (px)
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Повертає висоту зображення (px).
     * @return висота зображення (px)
     */
    public int getHeight() {
        return height;
    }

    /**
     * Встановлює висоту зображення (px).
     * @param height висота зображення
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Повертає тип зображення.
     * @return тип зображення
     */
    public int getPictureType() {
        return pictureType;
    }

    /**
     * Встановлює тип зображення.
     * @param pictureType тип зображення
     */
    public void setPictureType(final int pictureType) {
        this.pictureType = pictureType;
    }

    /**
     * Повертає назву файлу.
     * @return назва файлу
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Встановлює назву файлу.
     * @param filename назва файлу
     */
    public void setFilename(final String filename) {
        this.filename = filename;
    }
}
