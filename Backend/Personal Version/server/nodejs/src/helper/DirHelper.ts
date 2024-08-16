import fs from "fs";
/**
 * Creates directory, ignores if it already exists.
 * @param dir directory
 */
export function createDirectory(dir: string) {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }
}
