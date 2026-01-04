import axios from 'axios';
import Env from '../Env';
import {randomUUID} from "../services/UuidService.ts";

export const HttpClient = axios.create({
    baseURL: `${Env.API_BASE_URL}`,
    timeout: 30000,
    headers: {
        'X-Correlation-ID': randomUUID()
    }
});
