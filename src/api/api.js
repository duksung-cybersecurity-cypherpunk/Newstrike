import axios from "axios";

const api = axios.create({
  //원래 url
  baseURL: "baseURL 주소",
  imgBaseURL: "imgBaseURL 주소",
});

export default api;
