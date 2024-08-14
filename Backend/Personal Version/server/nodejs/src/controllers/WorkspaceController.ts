import { Request, Response } from "express";
import { DatabaseFactory } from "../database/Factory/DatabaseFactory";
import { IDatabaseAdapter } from "../database/IDatabaseAdapter";
import { IController } from "./IController";

export class WorkspaceController implements IController {
    private db!: IDatabaseAdapter;

    async initController(): Promise<void> {
        this.db = await DatabaseFactory.Build();
    }
    async foo(req: Request, res: Response): Promise<void> {
        res.send("FOO")
    }
}