package com.dongpv.sns.gateway.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author DongPV
 */
@Data
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder(value = {"status", "code", "message", "error"})
public class MultiRecordErrorResponseDtoBase implements BaseApiResponse {

    private static final String KEY_CODE = "code";
    private static final String KEY_ERROR = "error";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DETAIL = "detail";
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_PROJECT_DEFINED = "projectDefinedError";
    private static final String KEY_ROW_COUNT = "rowCount";

    @JsonProperty(KEY_STATUS)
    private boolean status = false;

    @Positive
    @JsonProperty(KEY_CODE)
    @XmlElement(name = KEY_CODE)
    private int code;

    @NotBlank
    @JsonProperty(KEY_MESSAGE)
    @XmlElement(name = KEY_MESSAGE)
    private String message;

    @JsonProperty(KEY_ERROR)
    private final ErrorDetail errorDetail = new ErrorDetail();

    @JsonIgnore
    @Setter(value = AccessLevel.NONE)
    @XmlTransient
    private int projectDefinedCnt = 0;

    /**
     * @param code
     * @param message
     */
    public MultiRecordErrorResponseDtoBase(@NotBlank String code, @NotBlank String message) {
        this(Integer.parseUnsignedInt(code), message);
    }

    /**
     * @param code
     * @param message
     */
    public MultiRecordErrorResponseDtoBase(@Positive int code, @NotBlank String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Add record-level detail for first record only. Use for single-record requests.
     *
     * @param field
     * @param value
     */
    public void addFirstRecordDetail(@NotBlank String field, @NotBlank String value) {
        addRecordDetail(field, value);
    }

    /**
     * Adds a project defined code and message for specified row/record.
     *
     * @param row
     * @param code
     * @param message
     */
    @SuppressWarnings({"unchecked", "hiding"})
    public void addPredefined(@Positive int row, @NotBlank String code, @NotBlank String message) {
        final Map<String, String> predefinedMap = new LinkedHashMap<>();
        predefinedMap.put(KEY_ID, code);
        predefinedMap.put(KEY_MESSAGE, String.format("row%d %s", row, message));

        errorDetail.getDetail().put(KEY_PROJECT_DEFINED, handlePredefinedList(predefinedMap));

        projectDefinedCnt = 1;
        errorDetail.getDetail().put(KEY_ROW_COUNT, errorDetail.getDetail().size() - 1 - projectDefinedCnt);
    }

    /**
     * Adds a project defined code and message for specified row/record.
     *
     * @param field Field Name/Param.
     * @param code Error Code.
     * @param message Error Message.
     */
    @SuppressWarnings({"unchecked", "hiding"})
    public void addPredefined(@NotBlank String field, @NotBlank String code, @NotBlank String message) {
        final Map<String, String> predefinedMap = new LinkedHashMap<>();
        predefinedMap.put(KEY_ID, code);

        if (field != null) {
            predefinedMap.put(KEY_MESSAGE, String.format("%s %s", field, message));
        } else {
            predefinedMap.put(KEY_MESSAGE, message);
        }

        errorDetail.getDetail().put(KEY_PROJECT_DEFINED, handlePredefinedList(predefinedMap));

        projectDefinedCnt = 1;
        errorDetail.getDetail().put(KEY_ROW_COUNT, errorDetail.getDetail().size() - 1 - projectDefinedCnt);
    }

    /**
     * Adds a project defined code and message for row/record 1.
     *
     * @param code
     * @param message
     */
    @SuppressWarnings("hiding")
    public void addPredefined(@NotBlank String code, @NotBlank String message) {
        addPredefined(1, code, message);
    }

    /**
     * Adds a project defined code and message with no specified row/field
     *
     * @param code Error Code.
     * @param message Error Message.
     */
    @SuppressWarnings({"unchecked", "hiding"})
    public void addPredefinedV2(@NotBlank String code, @NotBlank String message) {
        final Map<String, String> predefinedMap = new LinkedHashMap<>();
        predefinedMap.put(KEY_ID, code);
        predefinedMap.put(KEY_MESSAGE, message);

        errorDetail.getDetail().put(KEY_PROJECT_DEFINED, handlePredefinedList(predefinedMap));

        projectDefinedCnt = 1;
        errorDetail.getDetail().put(KEY_ROW_COUNT, errorDetail.getDetail().size() - 1 - projectDefinedCnt);
    }

    /**
     * Add individual record-level detail. Use for multi-record requests.
     *
     * @param field
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void addRecordDetail(@NotBlank String field, @NotBlank String value) {
        List<String> valueList;

        if (errorDetail.getDetail().containsKey(field)) {
            valueList = (List<String>) errorDetail.getDetail().get(field);
        } else {
            valueList = new ArrayList<>();
        }

        valueList.add(value);
        errorDetail.getDetail().put(field, valueList);

        errorDetail.setRowCount(errorDetail.getRowCount() + 1);
    }

    /**
     * @param key
     * @param value
     */
    public void addDetail(@NotBlank String key, @NotNull Object value) {
        errorDetail.getDetail().put(key, value);
    }

    private List<Map<String, String>> handlePredefinedList(Map<String, String> predefinedMap) {
        final List<Map<String, String>> predefinedList;

        if (errorDetail.getDetail().containsKey(KEY_PROJECT_DEFINED)) {
            predefinedList = (List<Map<String, String>>) errorDetail.getDetail().get(KEY_PROJECT_DEFINED);

        } else {
            predefinedList = new ArrayList<>();
        }

        predefinedList.add(predefinedMap);

        return predefinedList;
    }

    /**
     * Inner class to represent the error detail structure
     */
    @Data
    public static class ErrorDetail {
        @JsonProperty(KEY_ROW_COUNT)
        private int rowCount = 0;

        @JsonProperty(KEY_DETAIL)
        private final Map<String, Object> detail = new LinkedHashMap<>();
    }
}
