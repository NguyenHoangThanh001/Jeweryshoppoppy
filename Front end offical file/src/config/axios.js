import axios from "axios";
//const baseUrl = "https://jewerystorepoppy.online/api"; //Only for deployment
 const baseUrl = "http://localhost:8080/api";

const config = {
  baseUrl,
};
const api = axios.create(config);
api.defaults.baseURL = baseUrl;
const handleBefore = (config) => {
  const token = localStorage.getItem("token")?.replaceAll('"', "");
  config.headers["Authorization"] = `Bearer ${token}`;
  return config;
};

const handleError = (error) => {
  console.log(error);
};

api.interceptors.request.use(handleBefore, handleError);

export default api;
