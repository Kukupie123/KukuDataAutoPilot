import express from 'express';
import { createWorkspace, deleteWorkspace, getWorkspace, listWorkspaces, updateWorkspace } from "../controllers/WorkspaceController";

const router = express.Router();

// Define routes
router.post('/', createWorkspace); // Create Workspace
router.get('/:id', getWorkspace); // Get Workspace
router.put('/:id', updateWorkspace); // Update Workspace
router.delete('/:id', deleteWorkspace); // Delete Workspace
router.get('/', listWorkspaces); // List Workspaces

export default router;
