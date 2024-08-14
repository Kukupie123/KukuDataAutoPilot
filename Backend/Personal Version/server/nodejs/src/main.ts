import express from 'express';
import { WorkspaceRoute } from "./routes/WorkspaceRoute";

const app = express();
app.use(express.json()); // Middleware to parse JSON bodies

// routes
const workspaceRoute = new WorkspaceRoute();
app.use('/api/workspaces', workspaceRoute.router);

// Start the server
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
