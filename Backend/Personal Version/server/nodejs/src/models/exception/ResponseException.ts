/**
 * Exceptions that needs to be sent back as response needs to inherit this
 */

export class ResponseException extends Error {
    public status: number = -1;
    constructor(msg: string, status: number) {
        super(`${ResponseException.name} : ${msg}`);
        this.status = status;
    }
}