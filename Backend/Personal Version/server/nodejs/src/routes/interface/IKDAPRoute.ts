import { Request, Response } from "express";

export interface RouteDefinition {
    path: string;
    method: "get" | "post" | "put" | "delete" | "patch" | "options" | "head";
    handler: (req: Request, res: Response) => void; //Takes a function which returns void
}

export interface IKDAPRoute {
    initRouter(): Promise<void>;
    getRoutes(): RouteDefinition[];
}
