
import axios from "axios";

const API_URL = "http://localhost:8081";

export const registerUser = async (userData) => {
  return axios.post(`${API_URL}/register`, userData);};

export const getPublicKey = async (username) => {
  return axios.get(`${API_URL}/users/${username}/publicKey`);
};


export const sendMessage = async (messageData) => {
  return axios.post(`${API_URL}/messages/send`, messageData);
};


export const getInbox = async (username) => {
  return axios.get(`${API_URL}/messages/inbox/${username}`);
};
