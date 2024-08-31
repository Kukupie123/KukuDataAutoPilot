//Pouch Db config
export const rootDir = "data/db/pouch";
export const workspaceDbDir = `${rootDir}/workspace`;
export const recordDbDir = `${rootDir}/record`;
export const wsRecIndexDbDir = `${rootDir}/wsRecIndexDb`;
export const recWsIndexDbDir = `${rootDir}/recWsIndexDb`;

export function combineStrings(s1: string, s2: string): string {
    return `${s1};${s2}`;
}

export function breakStrings(combinedString: string): [string, string] {
    const [s1, s2] = combinedString.split(";")
    console.log(`Broke string ${combinedString} into ${s1} and ${s2}`)
    return [s1, s2];
}
