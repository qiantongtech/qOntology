<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

 qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 You may use qOntology for commercial purposes, but you may not remove, hide,
 modify, or replace the qOntology logo, copyright notices, license notices,
 or attribution information without a separate commercial license.

 White-label use, OEM distribution, rebranding, or presenting qOntology as
 another product requires separate commercial authorization from
 Jiangsu Qiantong Technology Co., Ltd.

 Business License: https://community.qontology.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->

<template>
    <el-aside
        :style="{
            width: `${leftWidth}px`,
            marginLeft: leftWidth == 0 ? '-15px' : '0px',
            '--qt-wrap-height': qtWrapheight
        }"
        class="left-pane"
    >
        <div class="search-wrapper">
            <el-input
                class="filter-tree"
                v-model="deptName"
                :placeholder="placeholder"
                clearable
                prefix-icon="Search"
            />
        </div>

        <div class="left-tree">
            <el-tree
                class="dept-tree"
                :data="processedData"
                :props="{ label: 'name', children: 'children' }"
                    :filter-node-method="filterNode"
                    ref="deptTreeRef"
                    node-key="id"
                    highlight-current
                    :default-expanded-keys="expandedKeys"
                    @node-click="handleNodeClick"
                    @node-expand="handleNodeExpand"
                    @node-collapse="handleNodeCollapse"
                    :default-expand-all="defaultExpand"
                >
                    <template #default="{ node, data }">
                        <span class="custom-tree-node">
                            <!-- 第一级 -->
                            <el-icon
                                class="iconimg colorxz"
                                v-if="node.expanded && node.level === 1"
                            >
                                <FolderOpened />
                            </el-icon>
                            <el-icon
                                class="iconimg colorxz"
                                v-if="!node.expanded && node.level === 1"
                            >
                                <Folder />
                            </el-icon>

                            <!-- 有子节点的所有层级 -->
                            <el-icon
                                class="iconimg colorxz"
                                v-if="node.expanded && node.childNodes.length && node.level > 1"
                            >
                                <FolderOpened />
                            </el-icon>
                            <el-icon
                                class="iconimg colorxz"
                                v-if="!node.expanded && node.childNodes.length && node.level > 1"
                            >
                                <Folder />
                            </el-icon>

                            <!-- 无子节点的节点 -->
                            <el-icon
                                class="zjiconimg colorwxz"
                                v-show="
                                    !node.isCurrent &&
                                    node.level != 1 &&
                                    (!node.childNodes.length || node.childNodes.length === 0)
                                "
                                :data-node="getNode(node)"
                            >
                                <Tickets />
                            </el-icon>
                            <el-icon
                                class="zjiconimg colorxz"
                                v-show="
                                    node.isCurrent &&
                                    node.level != 1 &&
                                    (!node.childNodes.length || node.childNodes.length === 0)
                                "
                            >
                                <Tickets />
                            </el-icon>

                            <el-tooltip
                                class="box-item"
                                effect="dark"
                                :content="node.label"
                                placement="top-start"
                                :disabled="node.label.length < 10"
                            >
                                <span class="treelabel" @click="getNode(node)">
                                    {{ node.label }}
                                </span>
                            </el-tooltip>

                            <!-- 操作入口 -->
                            <el-dropdown
                                v-if="editable"
                                trigger="click"
                                @command="(cmd) => handleCommand(cmd, data)"
                                @visible-change="(v) => handleDropdownVisibleChange(v, data.id)"
                            >
                                <span
                                    class="operation-trigger"
                                    :class="{ 'is-active': activeDropdownNodeId === data.id }"
                                    @click.stop
                                >
                                    <el-icon class="action-icon">
                                        <MoreFilled />
                                    </el-icon>
                                </span>
                                <template #dropdown>
                                    <el-dropdown-menu class="dept-tree-dropdown">
                                        <el-dropdown-item :icon="Plus" command="add"
                                            >新增子级</el-dropdown-item
                                        >
                                        <template v-if="data.id != '0'">
                                            <el-dropdown-item
                                                :icon="CopyDocument"
                                                command="addSibling"
                                                >新增同级</el-dropdown-item
                                            >
                                            <el-dropdown-item :icon="Edit" command="edit"
                                                >编辑</el-dropdown-item
                                            >
                                            <el-dropdown-item
                                                :icon="Delete"
                                                command="delete"
                                                class="delete-item"
                                                >删除</el-dropdown-item
                                            >
                                        </template>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </span>
                    </template>
                </el-tree>
        </div>
    </el-aside>

    <!-- 拖拽栏 -->
    <div class="resize-bar" @mousedown="startResize">
        <div class="resize-handle-sx">
            <el-icon v-if="leftWidth == 0" @click.stop="toggleCollapse" class="collapse-icon">
                <ArrowRight />
            </el-icon>
            <el-icon v-else class="collapse-icon" @click.stop="toggleCollapse">
                <ArrowLeft />
            </el-icon>
        </div>
    </div>
</template>

<script setup>
    import {
        ref,
        defineProps,
        defineEmits,
        watch,
        getCurrentInstance,
        onMounted,
        onUnmounted,
        nextTick
    } from 'vue';
    import {
        Plus,
        CopyDocument,
        Edit,
        Delete,
        MoreFilled,
        FolderOpened,
        Folder,
        Tickets,
        ArrowRight,
        ArrowLeft
    } from '@element-plus/icons-vue';

    const { proxy } = getCurrentInstance();
    const props = defineProps({
        deptOptions: Array,
        leftWidth: {
            type: Number,
            default: 300
        },
        placeholder: {
            type: String,
            default: '请输入部门名称'
        },
        defaultExpand: {
            type: Boolean,
            default: false
        },
        // 按页面配置默认展开层数，默认仍沿用原有的一层展开行为。
        defaultExpandLevel: {
            type: Number,
            default: 1
        },
        editable: {
            type: Boolean,
            default: false
        },
        title: {
            type: String,
            default: '类目'
        },
        api: {
            type: Object,
            default: () => ({})
        },
        extraParams: {
            type: Object,
            default: () => ({})
        }
    });
    const emit = defineEmits([
        'node-click',
        'update:deptName',
        'update:leftWidth',
        'node-add',
        'node-edit',
        'node-delete'
    ]);

    // 1. 初始化高度
    const qtWrapheight = ref('86vh');
    let resizeObserver = null;
    const processedData = ref([]);

    function handleNodeAdd(data) {
        emit('node-add', data);
    }

    function handleNodeAddSibling(data) {
        const parentId = data.parentId || '0';
        emit('node-add', { ...data, parentId });
    }

    function handleCommand(command, data) {
        switch (command) {
            case 'add':
                handleNodeAdd(data);
                break;
            case 'addSibling':
                handleNodeAddSibling(data);
                break;
            case 'edit':
                handleNodeEdit(data);
                break;
            case 'delete':
                handleNodeDelete(data);
                break;
        }
    }

    function handleNodeEdit(data) {
        emit('node-edit', data);
    }

    function handleNodeDelete(data) {
        if (props.api.del) {
            proxy.$modal
                .confirm('是否确认删除"' + data.name + '"？')
                .then(function () {
                    if (props.api.del) {
                        return props.api.del(data.id);
                    }
                })
                .then(() => {
                    proxy.$modal.msgSuccess('删除成功');
                    // 如果删除的是当前选中的节点，清空选中状态
                    if (currentNodeKey.value === data.id) {
                        currentNodeKey.value = null;
                        emit('node-click', {});
                    }
                    getDeptTree();
                });
        } else {
            emit('node-delete', data);
        }
    }

    function getDeptTree() {
        if (props.api.list) {
            props.api.list(props.extraParams).then((response) => {
                const tree = proxy.handleTree(response.data, 'id', 'parentId');
                processedData.value = [
                    {
                        name: props.title,
                        value: '',
                        id: 0,
                        children: tree
                    }
                ];
                const defaultExpanded = [0];
                if (expandedKeys.value.length === 0) {
                    expandedKeys.value = defaultExpanded;
                } else if (!expandedKeys.value.some((k) => String(k) === '0')) {
                    expandedKeys.value.unshift(0);
                }
                if (currentNodeKey.value) {
                    nextTick(() => {
                        deptTreeRef.value.setCurrentKey(currentNodeKey.value);
                    });
                }
            });
        } else if (props.deptOptions && props.deptOptions.length > 0) {
            processedData.value = props.deptOptions;
        }
    }

    watch(
        () => props.extraParams,
        (val) => {
            if (props.api.list) {
                getDeptTree();
            }
        },
        { deep: true }
    );

    watch(
        () => props.deptOptions,
        (val) => {
            if (!props.api.list && Array.isArray(val)) {
                processedData.value = val;
            }
        },
        { immediate: true }
    );

    onMounted(() => {
        if (props.api.list) {
            getDeptTree();
        }
    });

    // 高度监听逻辑
    const getQtWrapHeight = () => {
        const element = document.querySelector('.qt-wrap');
        if (element) {
            qtWrapheight.value = element.offsetHeight + 'px';
        } else {
            qtWrapheight.value = '86vh';
        }
    };

    onMounted(() => {
        getQtWrapHeight();

        const targetElement = document.querySelector('.qt-wrap');
        if (targetElement) {
            resizeObserver = new ResizeObserver(() => {
                getQtWrapHeight();
            });
            resizeObserver.observe(targetElement);
        }

        window.addEventListener('resize', getQtWrapHeight);
    });

    onUnmounted(() => {
        if (resizeObserver) {
            const targetElement = document.querySelector('.qt-wrap');
            if (targetElement) {
                resizeObserver.unobserve(targetElement);
            }
            resizeObserver.disconnect();
        }
        window.removeEventListener('resize', getQtWrapHeight);
    });

    const deptName = ref('');
    const deptTreeRef = ref(null);
    const leftWidth = ref(props.leftWidth);
    const expandedKeys = ref([]);
    const currentNodeKey = ref(null);
    const activeDropdownNodeId = ref(null);

    function handleDropdownVisibleChange(visible, nodeId) {
        activeDropdownNodeId.value = visible ? nodeId : null;
    }

    function handleNodeExpand(data) {
        if (!expandedKeys.value.includes(data.id)) {
            expandedKeys.value.push(data.id);
        }
    }

    function handleNodeCollapse(data) {
        const index = expandedKeys.value.indexOf(data.id);
        if (index > -1) {
            expandedKeys.value.splice(index, 1);
        }
    }

    function getIdsByLevel(nodes, level = 2, currentLevel = 1) {
        let ids = [];
        if (!nodes || currentLevel > level) return ids;

        for (const node of nodes) {
            ids.push(node.id);
            if (node.children && node.children.length > 0) {
                ids = ids.concat(getIdsByLevel(node.children, level, currentLevel + 1));
            }
        }
        return ids;
    }

    watch(
        () => props.deptOptions,
        (val) => {
            if (Array.isArray(val) && val.length > 0) {
                if (
                    val.length === 1 &&
                    val[0] &&
                    String(val[0].id) === '0' &&
                    props.defaultExpandLevel === 1 &&
                    Array.isArray(val[0].children)
                ) {
                    expandedKeys.value = [val[0].id];
                } else {
                    expandedKeys.value = getIdsByLevel(val, props.defaultExpandLevel);
                }
            }
        },
        { immediate: true }
    );

    const filterNode = (value, data) => {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
    };

    watch(deptName, (val) => {
        if (deptTreeRef.value) {
            deptTreeRef.value.filter(val);
        }
    });

    watch(
        () => props.leftWidth,
        (val) => {
            leftWidth.value = val;
        }
    );

    // 拖拽逻辑
    const isResizing = ref(false);
    let startX = 0;
    const startResize = (event) => {
        isResizing.value = true;
        startX = event.clientX;
        document.addEventListener('mousemove', updateResize);
        document.addEventListener('mouseup', stopResize);
    };
    const stopResize = () => {
        isResizing.value = false;
        document.removeEventListener('mousemove', updateResize);
        document.removeEventListener('mouseup', stopResize);
    };
    const updateResize = (event) => {
        if (isResizing.value) {
            const delta = event.clientX - startX;
            leftWidth.value += delta;
            startX = event.clientX;
        }
    };

    // 折叠展开
    const toggleCollapse = () => {
        leftWidth.value = leftWidth.value === 0 ? 300 : 0;
        emit('update:leftWidth', leftWidth.value);
    };

    function handleNodeClick(data) {
        currentNodeKey.value = data.id;
        emit('node-click', data);
    }

    const getNode = (node) => {
        console.log(node);
    };

    const resetTree = () => {
        if (deptTreeRef.value) {
            deptTreeRef.value.setCurrentKey(null);
        }
    };

    const setCurrentKey = (key) => {
        if (deptTreeRef.value) {
            deptTreeRef.value.setCurrentKey(key);
            currentNodeKey.value = key;
        }
    };

    defineExpose({ resetTree, getDeptTree, setCurrentKey, deptTreeRef });
</script>

<style scoped lang="scss">
// ==========================================
// SCSS 变量定义（颜色 / 字体 / 尺寸 / 间距）
// ==========================================

// 颜色变量
$bg-white: #ffffff;
$bg-gray: #f0f2f5;
$primary-color: var(--el-color-primary, #358cf3);
$primary-color-light: var(--qt-primary-light);
$text-primary: rgba(0, 0, 0, 0.85);
$text-secondary: #999;
$text-placeholder: #aaa;
$border-gray: #ccc;
$border-gray-light: #d6dae1;
$icon-selected: #358cf3;
$danger-color: var(--el-color-danger, #f56c6c);

// 字体变量
$font-family: PingFangSC-Regular, "PingFang SC", "Microsoft YaHei", Arial,
  sans-serif;
$font-size-base: 14px;
$font-size-sm: 12px;
$font-weight-normal: 400;
$font-weight-medium: 500;

// 尺寸变量
$aside-width-min: 0;
$aside-padding-y: 2px;
$tree-padding: 15px;
$node-padding-x: 12px;
$node-padding-right: 36px;
$icon-size-sm: 14px;
$icon-size-base: 16px;
$icon-size-md: 18px;
$icon-size-lg: 28px;
$collapse-icon-size: 28px;
$resize-bar-width: 15px;
$resize-handle-height: 50px;
$resize-handle-width: 5px;

// 间距变量
$spacing-xs: 2px;

// ==========================================
// 基础布局样式
// ==========================================
.left-pane {
  background-color: $bg-white;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0;
  margin-bottom: 0px;
  height: var(--qt-wrap-height);
}

// 保留类名以兼容可能的外部样式覆盖，但外观与 .left-pane 保持一致
.el-aside {
  background-color: $bg-white;
}

.left-tree {
  padding: 0 10px 12px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  box-shadow: none !important;
  background-color: $bg-white;
  border-radius: 0;
  width: auto;
  height: auto;
}

.left-tree::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}

.dept-tree {
  min-width: 100%;
  background: transparent;
  color: #333333;
}
.dept-tree :deep(.el-tree-node__content) {
  height: 36px;
  padding: 4px 2px;
}
.dept-tree :deep(.el-tree-node__content:hover) { background: #f5f7fa; }
.dept-tree :deep(.el-tree-node.is-current > .el-tree-node__content) { border: none; background: $primary-color-light; }
.dept-tree :deep(.el-tree-node.is-current > .el-tree-node__content .treelabel) { color: var(--el-color-primary); }

:deep(.dept-tree) {
  .el-tree-node__expand-icon {
    width: 14px;
    height: 24px !important;
    line-height: 24px !important;
    padding: 0 4px 0 0;
    color: transparent;
    transform: none !important;
    position: relative !important;
    right: auto !important;
    font-size: 14px !important;
    flex-shrink: 0;

    svg {
      display: none;
    }

    &::before {
      content: "";
      position: absolute;
      top: 50%;
      left: 50%;
      width: 4px;
      height: 4px;
      border-right: 1.5px solid #666;
      border-bottom: 1.5px solid #666;
      transform: translate(-60%, -50%) rotate(-45deg);
      transition: transform 0.16s ease;
    }

    &.expanded::before {
      transform: translate(-50%, -65%) rotate(45deg);
    }

    &.is-leaf::before {
      display: none;
    }
  }
}

// ==========================================
// 节点内容样式
// ==========================================
.custom-tree-node {
  width: calc(100% - 4px);
  min-width: 0;
  display: flex;
  flex: 1;
  align-items: center;
  padding-right: 12px;
  line-height: 1;
}

.colorxz {
  color: #358cf3;
}

.colorwxz {
  color: #afd1fa;
}

.treelabel {
  min-width: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: "PingFang SC", sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 18px;
  color: #333333;
  margin-left: 6px;
}

.zjiconimg {
  font-size: 12px;
}
.iconimg {
  font-size: 15px;
}

// ==========================================
// 操作下拉菜单
// ==========================================
.operation-trigger {
  opacity: 0;
  transition: opacity 0.15s ease;
  margin-left: auto;
  padding: 2px 4px;
  border-radius: 3px;
  border: 0;
  background: transparent;
  color: #687385;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  cursor: pointer;
  pointer-events: none;
  transform: rotate(90deg); /* 旋转 90 度实现竖向三个点 */

  &.is-active,
  &:hover {
    opacity: 1;
    pointer-events: auto;
    background-color: rgba(0, 0, 0, 0.04);
  }
}

.custom-tree-node:hover .operation-trigger {
  opacity: 1;
  pointer-events: auto;
}

.action-icon {
  font-size: 16px;
  color: #999;
}

// ==========================================
// 拖拽栏 & 折叠/展开
// ==========================================
.resize-bar {
  height: v-bind(qtWrapheight);
  cursor: ew-resize;
  background-color: $bg-gray;
  display: flex;
  align-items: center;
  justify-content: center;
  width: $resize-bar-width;
  flex-shrink: 0;
}

.resize-handle-sx {
  width: $resize-bar-width;
  text-align: center;
  position: relative;
}

.zjsx {
  display: none;
  width: $resize-handle-width;
  height: $resize-handle-height;
  border-left: 1px solid $border-gray;
  border-right: 1px solid $border-gray;
}

.collapse-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: $collapse-icon-size;
  color: #aaa;
  cursor: pointer;
  z-index: 10;
  padding: 5px;
}

// ==========================================
// 搜索框样式
// ==========================================
.search-wrapper {
  display: flex;
  flex-direction: column;
}

.head-container {
  position: relative;
  height: 32px !important;
  box-shadow: none !important;
  padding: 0;
  margin-bottom: 16px;
  background: transparent;
  flex-shrink: 0;
}

.tree-container, .tree-wrapper {
  position: relative;
  flex: 1;
  min-height: 100px;
}

.filter-tree {
  width: auto;
  margin: 10px 10px 15px !important;
}
</style>

<style lang="scss">
.dept-tree-dropdown {
  .dept-tree-menu-icon {
    width: 14px;
    height: 14px;
    margin-right: 8px;
    vertical-align: middle;
    display: inline-block;
  }
  .delete-item {
    color: var(--el-color-danger, #f56c6c);
  }
}
</style>
