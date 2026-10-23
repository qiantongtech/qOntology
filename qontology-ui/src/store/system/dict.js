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

const useDictStore = defineStore('dict', {
    state: () => ({
        dict: new Array()
    }),
    actions: {
        // 获取字典
        getDict(_key) {
            if (_key == null && _key == '') {
                return null;
            }
            try {
                for (let i = 0; i < this.dict.length; i++) {
                    if (this.dict[i].key == _key) {
                        return this.dict[i].value;
                    }
                }
            } catch (e) {
                return null;
            }
        },
        // 设置字典
        setDict(_key, value) {
            if (_key !== null && _key !== '') {
                this.dict.push({
                    key: _key,
                    value: value
                });
            }
        },
        // 删除字典
        removeDict(_key) {
            var bln = false;
            try {
                for (let i = 0; i < this.dict.length; i++) {
                    if (this.dict[i].key == _key) {
                        this.dict.splice(i, 1);
                        return true;
                    }
                }
            } catch (e) {
                bln = false;
            }
            return bln;
        },
        // 清空字典
        cleanDict() {
            this.dict = new Array();
        },
        // 初始字典
        initDict() {}
    }
});

export default useDictStore;
