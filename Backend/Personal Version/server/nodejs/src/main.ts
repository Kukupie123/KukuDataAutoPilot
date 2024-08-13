import express from 'express';
import workspaceRoutes from "./routes/WorkspaceRoute";

const app = express();
app.use(express.json()); // Middleware to parse JSON bodies

// routes
app.use('/api/workspaces', workspaceRoutes);

// Start the server
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
