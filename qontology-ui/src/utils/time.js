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

// time.js

import moment from "moment";

/** 将时间转换为「x分钟前」等相对时间描述，1 小时内展示为 HH:mm。 */
export function timeAgo(timeStr) {
  const date = new Date(timeStr);
  const now = new Date();
  const diff = (now - date) / 1000;

  if (diff < 60) {
    return moment(timeStr).format("HH:mm");
  } else if (diff < 3600) {
    return `${Math.floor(diff / 60)}分钟前`;
  } else if (diff < 86400) {
    return `${Math.floor(diff / 3600)}小时前`;
  } else if (diff < 2592000) {
    return `${Math.floor(diff / 86400)}天前`;
  } else if (diff < 31536000) {
    return `${Math.floor(diff / 2592000)}月前`;
  } else {
    return `${Math.floor(diff / 31536000)}年前`;
  }
}
