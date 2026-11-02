// 未配置独立 WebSocket 服务时，走当前站点的 /websocket/ 反向代理。
export function getMessageWebSocketUrl(userId, base = import.meta.env.VITE_APP_WEBSOCKET_API, origin = window.location.origin) {
    if (userId == null || String(userId).trim() === '' || userId === 'undefined' || userId === 'null') return null;
    const url = new URL(base || '/', origin);
    url.protocol = url.protocol === 'https:' || url.protocol === 'wss:' ? 'wss:' : 'ws:';
    url.pathname = `${url.pathname.replace(/\/$/, '')}/websocket/message/${encodeURIComponent(userId)}`;
    url.search = '';
    url.hash = '';
    return url.href;
}
