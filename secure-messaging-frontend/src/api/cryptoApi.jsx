import axios from "axios";
const api = "http://localhost:8080/api"

export async function encryptMessage(message) {
     const response = await axios.post(`${api}/encrypt`, { message });
    return response.data;

}


export async function decryptMessage(ciphertext, key, iv) {
     const response = await axios.post(`${api}/decrypt`, { ciphertext, key, iv });
  return response.data;
   
}