// 用于界面列表 key，兼容通过内网 HTTP 访问的环境。
let sequence = 0;
export function createRowKey() {
    if (typeof globalThis.crypto?.randomUUID === 'function') {
        return globalThis.crypto.randomUUID();
    }
    return `row-${Date.now()}-${++sequence}`;
}
