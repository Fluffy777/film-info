package com.fluffy.util;

import com.fluffy.dtos.ImageDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Допоміжний клас для роботи із документами docx через Apache POI.
 * @author Сивоконь Вадим
 */
public final class XWPFDocumentManipulator {
    private XWPFDocumentManipulator() { }

    /**
     * Позначка початку поля.
     */
    private static final String FIELD_BEGINS_WITH = "${";

    /**
     * Позначка завершення поля.
     */
    private static final String FIELD_ENDS_WITH = "}";

    // допоміжні методи для здійснення рекурсивного спуску
    private static void replaceTextInParagraphs(final List<XWPFParagraph> paragraphs, final String target, final String replacement) {
        for (XWPFParagraph p : paragraphs) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(target)) {
                        text = text.replace(target, replacement);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }

    private static void replaceTextInTables(final List<XWPFTable> tables, final String target, final String replacement) {
        if (tables == null || tables.isEmpty()) {
            return;
        }

        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    replaceTextInParagraphs(cell.getParagraphs(), target, replacement);
                    replaceTextInTables(cell.getTables(), target, replacement);
                }
            }
        }
    }

    /**
     * Замінює всі збіги із вказаним текстом на інші.
     * @param document документ
     * @param target текст, що замінюється
     * @param replacement текст, що замінює
     */
    public static void replaceText(final XWPFDocument document, final String target, final String replacement) {
        replaceTextInParagraphs(document.getParagraphs(), target, replacement);
        replaceTextInTables(document.getTables(), target, replacement);
    }

    // допоміжні методи для здійснення рекурсивного спуску
    private static boolean cellContainsContent(final XWPFTableCell cell, final String content) {
        for (XWPFParagraph p : cell.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(content)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static XWPFTable findContentInTables(final List<XWPFTable> tables, final String content) {
        if (tables == null || tables.isEmpty()) {
            return null;
        }

        XWPFTable result;
        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    if (cellContainsContent(cell, content)) {
                        return table;
                    } else {
                        result = findContentInTables(cell.getTables(), content);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Повертає першу таблицю в документі, що містить необхідний контекст.
     * @param document документ
     * @param content контент
     * @return таблиця із шуканим наповненням
     */
    public static XWPFTable getTableWithContent(final XWPFDocument document, final String content) {
        return findContentInTables(document.getTables(), content);
    }

    /**
     * Виконує зв'язування полів документа-шаблона із відповідними значеннями.
     * @param document документ
     * @param mapper словник відповідностей
     */
    public static void bindFields(final XWPFDocument document, final Map<String, String> mapper) {
        Set<String> keys = mapper.keySet();
        for (String key : keys) {
            String value = mapper.get(key);
            replaceText(document, FIELD_BEGINS_WITH + key + FIELD_ENDS_WITH, value);
        }
    }

    // допоміжні функції для здійснення рекурсивного спуску
    private static void bindImageToFieldInParagraphs(final List<XWPFParagraph> paragraphs, final String field, final ImageDTO imageDTO) {
        for (XWPFParagraph p : paragraphs) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(field)) {
                        text = text.replace(field, "");
                        r.setText(text, 0);
                        try (InputStream imageStream = new ByteArrayInputStream(imageDTO.getData())) {
                            r.addPicture(imageStream, imageDTO.getPictureType(), imageDTO.getFilename(), Units.pixelToEMU(imageDTO.getWidth()), Units.pixelToEMU(imageDTO.getHeight()));
                        } catch (InvalidFormatException | IOException e) {
                            // зображення не буде додано
                        }
                    }
                }
            }
        }
    }

    private static void bindImageToFieldInTables(final List<XWPFTable> tables, final String field, final ImageDTO imageDTO) {
        if (tables == null || tables.isEmpty()) {
            return;
        }

        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    bindImageToFieldInParagraphs(cell.getParagraphs(), field, imageDTO);
                    bindImageToFieldInTables(cell.getTables(), field, imageDTO);
                }
            }
        }
    }

    /**
     * Виконує зв'язування полів документа-шаблона із відповідним зображенням.
     * @param document документ
     * @param target назва поля
     * @param imageDTO DTO зображення
     */
    public static void bindImageToField(final XWPFDocument document, final String target, final ImageDTO imageDTO) {
        String field = FIELD_BEGINS_WITH + target + FIELD_ENDS_WITH;
        bindImageToFieldInParagraphs(document.getParagraphs(), field, imageDTO);
        bindImageToFieldInTables(document.getTables(), field, imageDTO);
    }

    /**
     * Видаляє всі параграфи, що містить клітина таблиці.
     * @param cell клітина таблиці
     */
    public static void removeAllParagraphs(final XWPFTableCell cell) {
        int size = cell.getParagraphs().size();
        for (int i = 0; i < size; ++i) {
            cell.removeParagraph(i);
        }
    }
}
