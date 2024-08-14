/**
 * pause operation for specified amount of time
 * @param ms milisecond to sleep for
 */
export function sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
}
