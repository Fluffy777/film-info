package com.fluffy.util;

import com.fluffy.dtos.ImageDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XWPFDocumentManipulator {
    private static String FIELD_BEGINS_WITH = "${";
    private static String FIELD_ENDS_WITH = "}";

    private static void replaceTextInParagraphs(List<XWPFParagraph> paragraphs, String target, String replacement) {
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

    private static void replaceTextInTables(List<XWPFTable> tables, String target, String replacement) {
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

    public static void replaceText(XWPFDocument document, String target, String replacement) {
        replaceTextInParagraphs(document.getParagraphs(), target, replacement);
        replaceTextInTables(document.getTables(), target, replacement);
    }


    private static boolean cellContainsContent(XWPFTableCell cell, String content) {
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

    private static XWPFTable findContentInTables(List<XWPFTable> tables, String content) {
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

    public static XWPFTable getTableWithContent(XWPFDocument document, String content) {
        return findContentInTables(document.getTables(), content);
    }

    public static void bindFields(XWPFDocument document, Map<String, String> mapper) {
        Set<String> keys = mapper.keySet();
        for (String key : keys) {
            String value = mapper.get(key);
            replaceText(document, FIELD_BEGINS_WITH + key + FIELD_ENDS_WITH, value);
        }
    }

    private static void bindImageToFieldInParagraphs(List<XWPFParagraph> paragraphs, String field, ImageDTO imageDTO) {
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

    private static void bindImageToFieldInTables(List<XWPFTable> tables, String field, ImageDTO imageDTO) {
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

    public static void bindImageToField(XWPFDocument document, String target, ImageDTO imageDTO) {
        String field = FIELD_BEGINS_WITH + target + FIELD_ENDS_WITH;
        bindImageToFieldInParagraphs(document.getParagraphs(), field, imageDTO);
        bindImageToFieldInTables(document.getTables(), field, imageDTO);
    }

    public static void removeAllParagraphs(XWPFTableCell cell) {
        int size = cell.getParagraphs().size();
        for (int i = 0; i < size; ++i) {
            cell.removeParagraph(i);
        }
    }
}
