import { Request, Response } from 'express';
import { WorkspaceModel } from '../models/WorkspaceModel';
import { DatabaseService } from '../services/database/DatabaseService';
import { DatabaseConfig } from '../services/database/config/DatabaseConfig';
import { IController } from './IController';

export class WorkspaceController implements IController {
    private dbService!: DatabaseService;

    async initializeController(): Promise<void> {
        try {
            /*
            The Singleton design pattern is used here to ensure that only one instance of the DatabaseService is active.
            This is crucial for resource management, as database connections can be costly. The method is asynchronous
            because establishing a database connection involves I/O operations that are not performed synchronously.
            */
            this.dbService = await DatabaseService.getInstance(new DatabaseConfig());
        } catch (error) {
            console.error("Error initializing DatabaseService:", error);
            throw new Error("Database service initialization failed");
        }
    }


    async foo(req: Request, resp: Response): Promise<void> {
        resp.send("FOO");
    }
}
