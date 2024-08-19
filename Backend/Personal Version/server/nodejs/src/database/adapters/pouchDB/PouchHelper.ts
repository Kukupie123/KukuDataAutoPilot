
//Pouch Db config
export const rootDir = "data/db/pouch";
export const workspaceDbDir = `${rootDir}/workspace`;
export const recordDbDir = `${rootDir}/record`;
export const userTableDir = `${rootDir}/user-table`;

export function generateProjectID(projectName: string, workspaceID: string): string {
    return `${workspaceID};${projectName}`;
}