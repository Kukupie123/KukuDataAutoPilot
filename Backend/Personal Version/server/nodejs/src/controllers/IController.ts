export interface IController {
    /**
     * Called by ControllerFactory during build
     */
    initController(): Promise<void>;
}