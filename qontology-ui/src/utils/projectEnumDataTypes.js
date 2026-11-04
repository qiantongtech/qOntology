import { listDataType } from "@/api/om/dataType/dataType";

// 当前接口不支持按枚举来源筛选，汇总分页后精确匹配项目枚举，避免全局同名枚举混入关联列表。
export async function loadProjectEnumReferences(params) {
  if (!params.versionId) return [];
  const rows = [];
  // 接口每页最多允许 100 条；超过一页时继续分页读取。
  const pageSize = 100;
  for (let pageNum = 1; ; pageNum += 1) {
    const response = await listDataType({ ...params, dataType: "5", pageNum, pageSize });
    const page = response.data.rows || [];
    rows.push(...page);
    if (!page.length || rows.length >= Number(response.data.total || 0)) break;
  }
  return rows.filter((row) =>
    String(row.versionId) === String(params.versionId) &&
    String(row.dataType) === "5" &&
    (row.enumSource || "PROJECT") === "PROJECT" &&
    (params.enumType === undefined || row.enumType === params.enumType),
  );
}
