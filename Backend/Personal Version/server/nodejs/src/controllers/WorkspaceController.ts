import { Request, Response } from 'express';

// Create Workspace
export const createWorkspace = async (req: Request, res: Response) => {

};

// Get Workspace
export const getWorkspace = async (req: Request, res: Response) => {
    res.send("Get Workspace");
};

// Update Workspace
export const updateWorkspace = async (req: Request, res: Response) => {

};

// Delete Workspace
export const deleteWorkspace = async (req: Request, res: Response) => {

};

// List Workspaces
export const listWorkspaces = async (req: Request, res: Response) => {
    res.send("List of workspaces")
};
