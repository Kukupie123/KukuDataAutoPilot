import express from 'express';
import { KDAPLogger } from './util/KDAPLogger';
import { ResponseException } from './models/exception/ResponseException';
import { Category } from './config/kdapLogger.config';
import { buildRoute } from './routes/builder/RouteBuilder';
import { WorkspaceRoute } from './routes/WorkspaceRoute';
import { RecordRoute } from './routes/RecordRoute';

const startServer = async () => {
    const logger = new KDAPLogger("MAIN");
    const app = express();

    //Setup middleware, routes and start server.
    try {
        app.use(express.json()); // Middleware to parse JSON bodies

        const router = express.Router();
        await buildRoute(router, WorkspaceRoute);
        await buildRoute(router, RecordRoute);
        app.use('/api', router);

        // Start
        const PORT = 3000;
        app.listen(PORT, () => {
            console.log(`Server is running on port ${PORT}`);
        });

    } catch (error) {
        logger.log(`Failed to initialize server: ${JSON.stringify(error)}`, Category.Error);
        process.exit(1); // Exit the process with an error code
    }

    //Setup Post Middle ware

    //ResponseException handling
    app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
        const e = err as ResponseException
        logger.log(`Status code of Response Exception is ${e.status}`, Category.Error);
        if (e.status === undefined) {
            logger.log(JSON.stringify(err), Category.Error)
            res.status(500).json({ msg: "unhandled exception" })
            return;
        }
        logger.log(`${e.message} with stack ${e.stack}`, Category.Error)
        res.status(e.status).json({ msg: "KDAP Server encountered an error", data: e.message })
    });
}

//TODO: Complete workspace and record service
//TODO: Refactor and clean up these services
//TODO: Refactor the code to use event driven architecture
/**
 * Now  we are going to go for event driven architecture refactor
 * Since event dispatcher has an dispatcher and listener its going to be something like
 * one for dispatching and other for listening
 * So now dispatchers can implement dispatcher and dispatch, while listener can listen
 */
startServer();
/**
 * 
 * 
 * 
 * 
 * 
 * {
 * "project name"
 * "workspace ID",
 * "structure(JSON)"
 * }
 * 
 * FOr structures we can have these primitive datatypes
 * int, float, text, date
 * 
 * 
 * What happens when we create record to a workspace?
 * 1. create a new record entry
 * 2. go to the workspace table and update the record
 * 3. create the dynamic record based on attributes provided in "user-defined-tables/workspace-name" directory.
 * 4. Task done
 * 
 * lets define the structure json now. Its simply a name vs type map
 * 
 * {
 * "_id":"text;mandatory" //ID is ALWAYS mandatory
 * "name":"text;mandatory;",
 * "age":"int;optional"
 * "DOB":"date;mandatory",
 * "weight" :"float;mandatory"
 * }
 * 
 * 
 */