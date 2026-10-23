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

import { Circle as CircleGeometry } from '@antv/g';
import { Renderer as SvgRenderer } from '@antv/g-svg';
import { Renderer as CanvasRenderer } from '@antv/g-canvas';
import { Circle, ExtensionCategory, Graph, register } from '@antv/g6';

// 呼吸效果
class BreathingCircle extends Circle {
    onCreate() {
        const halo = this.shapeMap.halo;
        halo.animate([{ lineWidth: 0 }, { lineWidth: 15 }], {
            duration: 1000,
            iterations: Infinity,
            direction: 'alternate'
        });
    }
}

register(ExtensionCategory.NODE, 'breathing-circle', BreathingCircle);
// 涟漪效果
class RippleCircle extends Circle {
    onCreate() {
        const { fill } = this.attributes;
        const r = this.shapeMap.key.style.r;
        const length = 3;
        const fillOpacity = 0.5;

        Array.from({ length }).map((_, index) => {
            const ripple = this.upsert(
                `ripple-${index}`,
                CircleGeometry,
                {
                    r,
                    fill,
                    fillOpacity
                },
                this
            );
            ripple.animate(
                [
                    { r, fillOpacity },
                    { r: r + length * 3, fillOpacity: 0 }
                ],
                {
                    duration: 1000 * length,
                    iterations: Infinity,
                    delay: 1000 * index,
                    easing: 'ease-cubic'
                }
            );
        });
    }
}

register(ExtensionCategory.NODE, 'ripple-circle', RippleCircle);

export class G6Network {
    constructor(dom, data, options) {
        if (!dom) throw new Error('no dom!');
        console.log(options, 'options');
        const step = 30;
        // 数据
        this.data = {
            nodes: data.nodes,
            edges: autoOffsetEdges(data.edges, step)
        };
        // 配置
        this.options = {
            // (判断是否开启动画)
            animation: this.data.nodes.length <= 200,
            //渲染方式
            renderer:
                this.data.nodes.length <= 200
                    ? () => new SvgRenderer()
                    : () => new CanvasRenderer(),
            // autoFit: "view",
            autoResize: true,
            // 极简配置，保证可交互
            grid: false,
            background: false,

            // 最简单的连线配置
            connecting: {
                router: 'normal', // 最简单的直线路由
                connector: 'normal',
                snap: false,
                allowBlank: true
            },

            // 基本交互
            interacting: {
                nodeMovable: true,
                edgeMovable: false
            },

            rendering: {
                //画质
                quality: 'low', // 根据节点调整画质
                virtualRenderingBuffer: this.data.nodes.length >= 5000 ? 0.5 : 1, // 预加载视口周围50%区域的节点
                enableVirtualRendering: this.data.nodes.length > 200 ? true : false, //虚拟渲染
                enableOptimizedRendering: this.data.nodes.length <= 200 ? true : false, // 小数据量开启以获得平滑动画；大数据量如果遇到问题可关闭，通常保持 true 即可
                // 动态调整 overscan 数量
                overscanCount: this.data.nodes.length >= 5000 ? 5 : 10
            },

            scroller: {
                enabled: true,
                pannable: true
            },
            node: {
                type: 'circle',
                style: {
                    halo: true,
                    labelWordWrap: true,
                    labelMaxWidth: '300',
                    labelFill: '#DAE6FD',
                    labelPlacement: 'bottom',
                    labelText: (d) => d.name,
                    labelFontSize: 10,
                    size: (d) => d.size,
                    ports: []
                },
                state: {
                    // 高亮
                    highlight: {
                        size: (d) => {
                            return d.size * 2;
                        },
                        halo: true,
                        lineWidth: 0
                    },
                    click: {
                        halo: true,
                        haloLineWidth: 20,
                        stroke: '#0036ff',
                        lineWidth: 4
                    },
                    dim: {
                        fill: '#99ADD1'
                    }
                },
                palette: {
                    type: 'group',
                    field: 'cluster'
                }
            },
            edge: {
                type: 'quadratic',
                state: {
                    // 高亮
                    highlight: {
                        stroke: '#0076ff'
                    }
                },
                style: {
                    lineWidth: (d) => d.lineWidth || 1,
                    labelText: (d) => d.name,
                    labelFill: '#7DBFFA',
                    labelFontSize: 8,
                    labelAutoRotate: true,
                    labelTextBaseline: (d) => (d.curveOffset < 0 ? 'bottom' : 'top'),
                    endArrow: true,
                    curveOffset: (d) => d.curveOffset
                },
                palette: {
                    type: 'group',
                    field: 'cluster'
                }
            },
            layout: {
                type: 'd3-force',
                link: {
                    distance: () => {
                        return 100;
                    },
                    strength: () => {
                        return 0.2;
                    }
                },
                manyBody: {
                    strength: () => {
                        return -180;
                    }
                },
                preventOverlap: true
            },
            plugins: [
                {
                    type: 'fullscreen',
                    key: 'fullscreen'
                }
            ],
            behaviors: [
                'zoom-canvas',
                'drag-canvas',
                // 根据节点数量动态选择拖拽行为

                this.data.nodes.length <= 200
                    ? {
                          type: 'drag-element-force'
                      }
                    : this.data.nodes.length <= 5000
                      ? {
                            type: 'drag-element' // 普通拖拽，性能更好
                        }
                      : {},
                {
                    type: 'hover-activate',
                    enable: (event) => event.target.type === 'node',
                    degree: 1, // 👈🏻 Activate relations.
                    state: 'highlight',
                    inactiveState: 'dim',
                    onHover: (event) => {
                        event.view.setCursor('pointer');
                    },
                    onHoverEnd: (event) => {
                        event.view.setCursor('default');
                    }
                },
                {
                    type: 'click-select',
                    key: 'clickControl',
                    state: 'click',
                    onClick: (d) => {
                        console.log(d, '===dd');
                    }
                }
            ],
            ...options
        };
        // 实例
        this.dom = dom;
        this.instance = null;
        this.init();
    }

    async init() {
        if (!this.dom) return;
        // 销毁旧实例
        if (this.instance) {
            this.instance.destroy();
        }
        // 创建新实例
        this.instance = new Graph({
            container: this.dom,
            data: this.data,
            ...this.options
        });
        await this.instance.render();
    }

    // 事件
    on(event, callback) {
        this.instance.on(event, callback);
    }

    // 重置颜色
    resetColor() {
        if (!this.instance || !this.data?.nodes) return;

        const updates = this.data.nodes.map((item) =>
            // 清除单个元素的所有状态
            this.instance.setElementState(item.id, [])
        );

        // this.instance.updateNodeData(updates);
        this.instance.draw(); // 确保更新生效
    }

    zoomBy(data) {
        this.instance.focusElement(data.id);
    }
    // 在 G6Network 类中添加
    showElement(id) {
        if (!this.instance) return;
        this.instance.showElement(id);
    }

    hideElement(id) {
        if (!this.instance) return;
        this.instance.hideElement(id);
    }

    // 视图调整方法
    fitView() {
        if (!this.instance) return;
        this.instance.fitView();
    }

    getZoom() {
        if (!this.instance) return 1;
        return this.instance.getZoom();
    }

    getPosition() {
        if (!this.instance) return [0, 0];
        return this.instance.getPosition();
    }
    // 销毁资源
    destroy() {
        if (this.instance) {
            this.instance.destroy();
            this.instance = null;
        }
    }
}

/**
 * 为 edges 自动分配 curveOffset，避免标签重叠
 * @param {Array} edges - 原始边数据
 * @param {number} step - 偏移步长（默认 15）
 * @returns {Array} 处理后的边数据
 */
function autoOffsetEdges(edges, step = 15) {
    // 按 source-target 分组
    const edgeGroups = {};

    edges.forEach((edge) => {
        const key = [edge.source, edge.target].sort().join('-');
        if (!edgeGroups[key]) edgeGroups[key] = [];
        edgeGroups[key].push(edge);
    });

    // 为每组分配不同偏移
    Object.values(edgeGroups).forEach((group) => {
        if (group.length === 1) {
            // 单条边：默认偏移
            group[0].curveOffset = step;
        } else {
            // 多条边：交替正负偏移
            group.forEach((edge, index) => {
                // 正负交替：15, -15, 30, -30, 45, -45...
                const offset = (Math.floor(index / 2) + 1) * step * (index % 2 === 0 ? 1 : -1);
                edge.curveOffset = offset;

                // 可选：为重要边添加标记
                if (edge.importance > 5) {
                    edge.isImportant = true;
                }
            });
        }
    });

    return edges;
}
