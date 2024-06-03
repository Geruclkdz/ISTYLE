import axios from 'axios';


const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true
});

axiosInstance.interceptors.request.use(function (config) {
    const token = localStorage.getItem('token');
    config.headers.Authorization = token ? `Bearer ${token}` : '';
    return config;
}, function (error) {
    return Promise.reject(error);
});

axiosInstance.interceptors.response.use(response => response, error => {
    if (error.response && error.response.status === 403) {
        localStorage.removeItem('token');
        window.location.href = '/login';
    }
    return Promise.reject(error);
});

export default axiosInstance;