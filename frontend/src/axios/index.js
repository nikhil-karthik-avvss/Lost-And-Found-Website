import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:9000/api", // Backend API base URL
  timeout: 10000 // Optional: set request timeout
});

export default api;
