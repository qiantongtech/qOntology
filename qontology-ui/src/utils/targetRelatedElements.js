import { listType as listObjectTypes } from "@/api/om/object/type";
import { listRelations } from "@/api/om/object/relation";

// 主列表和对象类型详情共用相同的关联元素统计口径。
export async function withTargetRelatedElements(rows, versionId) {
  return Promise.all(rows.map(async (row) => {
    const query = {
      versionId: versionId || row.versionId,
      targetIdList: [row.id],
      pageNum: 1,
      pageSize: 1,
    };
    const results = await Promise.allSettled([
      listObjectTypes(query),
      listRelations(query),
    ]);
    const labels = ["对象类型", "对象类型关系"];
    return {
      ...row,
      relatedElementTags: results.map((result, index) => {
        const count = result.status === "fulfilled" ? result.value.data?.total : null;
        return `${labels[index]}${count ?? "-"}`;
      }),
    };
  }));
}
