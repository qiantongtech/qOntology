import { listType } from '@/api/om/object/type';
import { listAttributes } from '@/api/om/object/attribute';
import { listRelations } from '@/api/om/object/relation';

// 查询当前项目版本的全部对象类型，并保持 qKnow 映射组件的数据结构。
export function getExtSchemaAllList(query = {}) {
    return listType({ pageNum: 1, pageSize: -1, versionId: query.versionId }).then((res) => ({
        ...res,
        data: res?.data?.rows || res?.data?.list || []
    }));
}

// 查询对象类型属性，并适配 qKnow 原有的 schemaId 参数。
export function listAttribute(query = {}) {
    return listAttributes({ pageNum: 1, pageSize: 100, versionId: query.versionId,
        objectTypeId: query.schemaId || query.objectTypeId }).then((res) => {
        const list = res?.data?.rows || res?.data?.list || [];
        return { ...res, data: { ...(res?.data || {}), list, rows: list } };
    });
}

// 查询对象类型关系，并适配 qKnow 原有字段名。
export function listRelation(query = {}) {
    return listRelations({ pageNum: 1, pageSize: 100, versionId: query.versionId }).then((res) => {
        const rows = res?.data?.rows || res?.data?.list || [];
        const startTypeId = query.startSchemaId || query.startTypeId;
        const list = rows.filter((item) => !startTypeId || String(item.startTypeId) === String(startTypeId))
            .map((item) => ({ ...item, relation: item.name }));
        return { ...res, data: { ...(res?.data || {}), list, rows: list } };
    });
}
