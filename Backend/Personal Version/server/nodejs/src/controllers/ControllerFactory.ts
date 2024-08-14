import { IController } from "./IController";

// src/factories/ControllerFactory.ts
export class ControllerFactory {
    /*
    { new(...args: any[]): T } syntax represents a constructor signature in a type or interface. This pattern is useful for defining types for class constructors, allowing you to create instances of classes generically.

    Here's a breakdown of what each part means:
    new(...args: any[]): T
    new: This keyword indicates that you're describing the constructor of a class.
    (...args: any[]): This represents the parameters that the constructor takes. ...args means it can accept any number of arguments of any type.
    T: This is the return type of the constructor. It specifies that the constructor will create an instance of type T.
    */
    public static async BUILD<T extends IController>(ControllerClass: { new(...args: any[]): T }): Promise<T> {
        const a = new ControllerClass();
        await a.initializeController(); //initialize the controller before returning it to make sure every async operation is complete before returning
        return a;
    }
}
