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

import { DataSet, Timeline } from 'vis-timeline/standalone';
import 'vis-timeline/styles/vis-timeline-graph2d.css';
import moment from 'moment';
// 需要引入下方这个文件
import 'moment/dist/locale/zh-cn.js';
export class TimeLine {
    constructor(dom, data, options) {
        if (!dom) throw new Error('no dom!');

        // 大数据量优化
        const nodeCount = data?.nodes?.length || 0;
        const edgeCount = data?.edges?.length || 0;
        const totalCount = nodeCount + edgeCount;
        const isLargeData = totalCount >= 200;

        // 配置
        this.options = {
            locale: 'zh-cn', // 时间轴国际化
            zoomable: true,
            moveable: true,
            editable: false,
            orientation: 'top',
            height: '100%',
            showCurrentTime: true, // 显示当前时间
            zoomMax: 1000 * 60 * 60 * 24 * 365,
            zoomMin: 1000 * 60,
            moment: function (date) {
                return moment(date).locale('zh-cn'); //vis.moment(date).utcOffset('+08:00');
            },
            // 显式将此选项设置为true以完全禁用Timeline的XSS保护
            xss: {
                disabled: true
            },
            // 大数据量优化配置
            maxHeight: isLargeData ? '80%' : '100%',
            horizontalScroll: isLargeData ? true : false,
            zoomKey: isLargeData ? '' : 'ctrlKey',
            ...options
        };
        // 数据
        this.data = data;
        // 实例
        this.dom = dom;
        this.instance = null;
        this.init();
    }

    init() {
        if (!this.dom) return;
        // 销毁旧实例
        if (this.instance) {
            this.instance.destroy();
        }
        // 创建新实例
        const items = new DataSet(this.extractEvents(this.data));
        this.instance = new Timeline(this.dom, items, this.options);
    }
    // 提取data
    extractEvents(data) {
        const events = [];
        const nodeCount = data?.nodes?.length || 0;
        const edgeCount = data?.edges?.length || 0;
        const totalCount = nodeCount + edgeCount;
        const isLargeData = totalCount >= 200;

        // 大数据量时限制显示数量
        const maxEvents = isLargeData ? 100 : Infinity;
        let eventCount = 0;

        data?.nodes.forEach((node) => {
            if (node.createTime && eventCount < maxEvents) {
                events.push({
                    id: `node-${node.id}`,
                    data: node,
                    start: new Date(node.createTime),
                    content: node.label || node.id
                    // type: "node",
                });
                eventCount++;
            }
        });
        data?.edges.forEach((edge) => {
            if (edge.createTime && eventCount < maxEvents) {
                events.push({
                    id: `edge-${edge.source}-${edge.target}`,
                    start: new Date(edge.createTime),
                    content: `${edge.source} - ${edge.label || '关联'} - ${edge.target}`
                    // type: "edge",
                });
                eventCount++;
            }
        });
        console.log('🚀 ~ TimeLine ~ extractEvents ~ events:', events);
        return events;
    }
    // 事件
    on(event, callback) {
        this.instance.on(event, callback);
    }

    // 销毁资源
    destroy() {
        if (this.instance) {
            this.instance.destroy();
        }
    }
}
