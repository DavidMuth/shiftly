import { type AxiosResponse } from "axios";
import apiClient from "./ApiAxios";


export default class BaseApi<
    TList = any,      // Type returned for list()
    TSingle = any,    // Type returned for get()
    TCreate = any,    // Type for create(payload)
    TUpdate = any     // Type for update(payload)
> {
    constructor(private resource: string) { }

    list(params?: Record<string, any>): Promise<AxiosResponse<TList>> {
        return apiClient.get(this.resource, { params });
    }

    get(id: string | number): Promise<AxiosResponse<TSingle>> {
        return apiClient.get(`${this.resource}/${id}`);
    }

    create(payload: TCreate): Promise<AxiosResponse<TSingle>> {
        return apiClient.post(this.resource, payload);
    }

    update(id: string | number, payload: TUpdate): Promise<AxiosResponse<TSingle>> {
        return apiClient.put(`${this.resource}/${id}`, payload);
    }

    delete(id: string | number): Promise<AxiosResponse<void>> {
        return apiClient.delete(`${this.resource}/${id}`);
    }
}
