package com.fluffy.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Клас будівника URL-адреси.
 * @author Сивоконь Вадим
 */
public class URLBuilder {
    /**
     * Будівник, що зберігає ієрархію директорій.
     */
    private StringBuilder folders;

    /**
     * Будівник, що зберігає параметри та їх значення.
     */
    private StringBuilder params;

    /**
     * Протокол, який використовується для запиту.
     */
    private String protocol;

    /**
     * Домен.
     */
    private String host;

    /**
     * Фрагмент сторінки.
     */
    private String fragment;

    /**
     * Створює порожній об'єкт будівника URL-адреси.
     */
    public URLBuilder() {
        folders = new StringBuilder();
        params = new StringBuilder();
    }

    /**
     * Створює об'єкт будівника URL-адреси на основі вказаного домену.
     * @param host домен
     */
    public URLBuilder(String host) {
        this();
        this.host = host;
    }

    /**
     * Створює об'єкт будівника URL-адреси на основі вказаного протоколу та
     * домену.
     * @param protocol протокол
     * @param host домен
     */
    public URLBuilder(String protocol, String host) {
        this(host);
        this.protocol = protocol;
    }

    /**
     * Встановлює протокол.
     * @param protocol протокол
     * @return об'єкт будівника
     */
    public URLBuilder setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * Повертає протокол.
     * @return протокол
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Встановлює домен.
     * @param host домен
     * @return об'єкт будівника
     */
    public URLBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Повертає домен.
     * @return домен
     */
    public String getHost() {
        return host;
    }

    /**
     * Встановлює фрагмент.
     * @param fragment фрагмент
     * @return об'єкт будівника
     */
    public URLBuilder setFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * Повертає фрагмент.
     * @return фрагмент
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * Додає директорію в ієрархію.
     * @param folder назва директорії
     * @return об'єкт будівника
     */
    public URLBuilder addSubfolder(String folder) {
        folders.append("/");
        folders.append(folder);
        return this;
    }

    // метод для внутрішнього використання
    private void addParameter0(String parameter) {
        if (params.length() > 0) {
            params.append("&");
        }
        params.append(parameter);
        params.append("=");
    }

    /**
     * Додає параметр за його значення.
     * @param parameter назва параметра
     * @param value значення
     * @return об'єкт будівника
     */
    public URLBuilder addParameter(String parameter, String value) {
        addParameter0(parameter);
        params.append(value);
        return this;
    }

    /**
     * Додає параметр за його значення.
     * @param parameter назва параметра
     * @param value значення
     * @return об'єкт будівника
     */
    public URLBuilder addParameter(String parameter, int value) {
        addParameter0(parameter);
        params.append(value);
        return this;
    }

    /**
     * Додає параметр за його значення.
     * @param parameter назва параметра
     * @param value значення
     * @return об'єкт будівника
     */
    public URLBuilder addParameter(String parameter, Object value) {
        addParameter0(parameter);
        params.append(value);
        return this;
    }

    /**
     * Повертає побудований абсолютний URL.
     * @return абсолютний URL
     */
    public String getURL() {
        try {
            return new URI(protocol, host, folders.toString(), params.toString(), fragment).toString();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    /**
     * Повертає відносну URL-адресу.
     * @return відносний URL
     */
    public String getRelativeURL() {
        try {
            return new URI(null, null, folders.toString(), params.toString(), fragment).toString();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    /**
     * Повертає рядкове представлення URL-будівника (абсолютну URL-адресу).
     * @return абсолютний URL
     */
    @Override
    public String toString() {
        return getURL();
    }
}
