import axios from 'axios';


const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true
});

axiosInstance.interceptors.request.use(function (config) {
    const url = (config.url || '').toString();
    const isImage = url.startsWith('/images') || url.includes('/images/');

    // Do not attach Authorization for public static images
    if (isImage) {
        return config;
    }

    const token = localStorage.getItem('token');
    config.headers = config.headers || {};
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        delete config.headers.Authorization;
    }
    return config;
}, function (error) {
    return Promise.reject(error);
});

// axiosInstance.interceptors.response.use(response => response, error => {
//     if (error.response && error.response.status === 403) {
//         localStorage.removeItem('token');
//         window.location.href = '/login';
//     }
//     return Promise.reject(error);
// });

export default axiosInstance;
