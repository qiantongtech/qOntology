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

import Vis from 'vis-network/dist/vis-network.min.js';
import 'vis-network/dist/dist/vis-network.min.css';
export const DEFAULT_OPTIONS = {
    autoResize: true,
    interaction: {
        hover: true
    },
    clickToUse: false,
    layout: {
        improvedLayout: true
    },
    nodes: {
        shape: 'dot',
        size: 10,
        borderWidth: 0,
        color: {
            background: '#315790',
            border: '#314790',
            highlight: {
                background: '#89b8ff',
                border: '#89b8ff4d'
            },
            hover: {
                background: '#89b8ff',
                border: '#89b8ff4d'
            }
        },
        font: {
            size: 14,
            color: '#d7e7ff'
        },
        shapeProperties: {
            useBorderWithImage: true
        },
        label: ''
    },
    edges: {
        width: 1,
        length: 150,
        arrows: {
            to: {
                enabled: true,
                scaleFactor: 1
            }
        },
        arrowStrikethrough: false,
        color: {
            color: '#89b8ff',
            highlight: '#89b8ff',
            hover: '#89b8ff'
        },
        hoverWidth: 2,
        smooth: {
            enabled: true,
            type: 'dynamic',
            forceDirection: 'none'
        },
        selectionWidth: 4,
        font: {
            size: 14,
            color: '#89b8ff',
            strokeWidth: 0,
            align: 'top'
        }
    },
    physics: {
        enabled: true,
        stabilization: false,
        wind: { x: 0, y: 0 },
        barnesHut: {
            gravitationalConstant: -2000,
            springConstant: 0.1,
            damping: 0.08
        }
    }
};
export class VisNetwork {
    constructor(dom, data, options) {
        if (!dom) throw new Error('no dom!');
        // 数据
        this.data = data;
        this.graphData = {
            nodes: [],
            edges: []
        };

        // 大数据量优化
        const nodeCount = data.nodes.length;
        const isLargeData = nodeCount >= 200;
        const isVeryLargeData = nodeCount >= 5000;

        // 动态配置
        const optimizedOptions = {
            interaction: {
                hover: true,
                dragNodes: !isVeryLargeData,
                dragView: true,
                zoomView: true
            },
            nodes: {
                ...DEFAULT_OPTIONS.nodes,
                size: isLargeData ? 6 : DEFAULT_OPTIONS.nodes.size,
                borderWidth: isLargeData ? 0 : DEFAULT_OPTIONS.nodes.borderWidth,
                font: {
                    ...DEFAULT_OPTIONS.nodes.font,
                    size: isLargeData ? 10 : DEFAULT_OPTIONS.nodes.font.size
                }
            },
            edges: {
                ...DEFAULT_OPTIONS.edges,
                length: isLargeData ? 80 : DEFAULT_OPTIONS.edges.length,
                arrows: {
                    ...DEFAULT_OPTIONS.edges.arrows,
                    to: {
                        ...DEFAULT_OPTIONS.edges.arrows.to,
                        scaleFactor: isLargeData ? 0.5 : DEFAULT_OPTIONS.edges.arrows.to.scaleFactor
                    }
                },
                smooth: {
                    ...DEFAULT_OPTIONS.edges.smooth,
                    enabled: !isVeryLargeData
                },
                font: {
                    ...DEFAULT_OPTIONS.edges.font,
                    size: isLargeData ? 10 : DEFAULT_OPTIONS.edges.font.size
                }
            },
            physics: {
                ...DEFAULT_OPTIONS.physics,
                enabled: !isLargeData,
                stabilization: data.edges.length > 100 ? true : false
            }
        };

        // 配置
        this.options = {
            ...DEFAULT_OPTIONS,
            ...optimizedOptions,
            ...options
        };
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
        this.extractEvents(this.data);
        // 创建新实例
        this.instance = new Vis.Network(this.dom, this.graphData, this.options);
    }
    // 提取data
    extractEvents(data) {
        this.graphData.nodes = new Vis.DataSet(data.nodes);
        this.graphData.edges = new Vis.DataSet(data.edges);
    }

    // node事件
    nodes(type, data) {
        this.graphData.nodes[type](data);
    }

    // edge事件
    edges(type, data) {
        this.graphData.edges[type](data);
    }

    // 画布事件
    on(event, callback) {
        this.instance.on(event, callback);
    }

    // 重置颜色
    resetColor() {
        this.data.nodes.forEach((item) => {
            this.graphData.nodes.update([
                {
                    id: item.id,
                    step: 0,
                    borderWidth: item?.step == 0 ? 0 : 15,
                    size: 10,
                    color: {
                        background: item?.rowBackground,
                        border: item?.rowBorder
                    }
                }
            ]);
        });
    }

    // 定位
    zoomBy(data) {
        this.moveTo({
            position: this.instance.getPosition(data.id),
            scale: 1.3,
            animation: {
                duration: 300,
                easingFunction: 'linear'
            }
        });
    }

    // 移动画布
    moveTo(data) {
        this.instance.moveTo({ ...data });
    }

    updateLayout(options) {
        this.instance.setOptions(options);
    }

    // 销毁资源
    destroy() {
        if (this.instance) {
            this.instance.destroy();
            this.instance = null;
        }
    }
}
