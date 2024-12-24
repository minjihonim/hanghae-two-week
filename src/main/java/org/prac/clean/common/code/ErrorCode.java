package org.prac.clean.common.code;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCES("0000", "등록 성공"),
    FAIL("1000", "정원 초과입니다.")
    ;

    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
