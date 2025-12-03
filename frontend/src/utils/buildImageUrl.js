const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Helper to build full image URL (handles API base prefix and optional cache-busting)
function pickCandidate(src) {
    if (!src) return null;
    if (typeof src === 'string') return src;
    // try common object shapes
    const candidates = [
        src.photo,
        src.user_photo,
        src.user?.photo,
        src.avatar,
        src.profilePhoto,
        src.url,
        src.link,
    ];
    for (const c of candidates) {
        if (typeof c === 'string' && c.length > 0) return c;
    }
    return null;
}

export default function buildImageUrl(imageSrc, { cacheBust = false } = {}) {
    const candidate = pickCandidate(imageSrc);
    if (!candidate) return null;

    // if it's already a data URL, return as-is
    if (candidate.startsWith('data:')) return candidate;

    const baseSrc = candidate.startsWith('http')
        ? candidate
        : `${API_BASE}${candidate.startsWith('/') ? '' : '/'}${candidate}`;

    // skip cache-bust for placeholder domains
    if (!cacheBust || baseSrc.includes('via.placeholder.com')) return baseSrc;

    const sep = baseSrc.includes('?') ? '&' : '?';
    return `${baseSrc}${sep}t=${Date.now()}`;
}
