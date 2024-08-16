import fs from "fs";
export class DirectoryHelper {
    /**
     * Creates directory, ignores if it already exists.
     * @param dir directory
     */
    public static CreateDirectory(dir: string) {
        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir, { recursive: true });
        }
    }
}