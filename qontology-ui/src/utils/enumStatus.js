// 复用项目状态字典的启用/禁用文案及标签样式，适配枚举已有的反向状态编码。
// 项目：1 启用、0 禁用；枚举：0 启用、1 禁用。
export function toEnumStatusOptions(projectStatusOptions) {
  return projectStatusOptions.map((option) => ({
    ...option,
    value: String(option.value) === "1" ? "0" : "1",
  }));
}
