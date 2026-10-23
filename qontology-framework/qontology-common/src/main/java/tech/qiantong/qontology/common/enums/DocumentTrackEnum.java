/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.common.enums;

/**
 * 知识文件跟踪
 * @author jw
 * @date 2025/06/26 16:54
 * @return
 */
public enum DocumentTrackEnum {
    PREVIEW(0, "preview"),
    DOWNLOAD(1, "download"),
    ;

    public final Integer code;
    public final String info;

    DocumentTrackEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public static DocumentTrackEnum get(Integer code) {
        for (DocumentTrackEnum v : values()) {
            if (v.eq(code)) {
                return v;
            }
        }
        return null;
    }

    public boolean eq(Integer code) {
        return this.code.equals(code);
    }

    public static String getInfo(Integer code) {
        return DocumentTrackEnum.get(code).getInfo();
    }

    public static Integer getCode(String info) {
        for (DocumentTrackEnum v : values()) {
            if (v.getInfo().equals(info)) {
                return v.getCode();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
