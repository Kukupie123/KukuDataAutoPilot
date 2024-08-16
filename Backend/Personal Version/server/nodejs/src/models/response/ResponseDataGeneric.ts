import { IResponseData } from "./IResponseData";

export class ResponseDataGeneric<T> implements IResponseData {
    private data: T;

    constructor(data: T) {
        this.data = data;
    }

    getData(): T {
        return this.data;
    }
}
