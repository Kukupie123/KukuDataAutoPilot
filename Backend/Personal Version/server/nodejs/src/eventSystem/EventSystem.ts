import { EventEmitter } from "events";
import { KDAPLogger } from "../util/KDAPLogger";

///Custom event system created which uses classes to enforce static events


// Base class for custom events
export abstract class CustomEvent<T> {
    constructor(
        public readonly eventName: string,
        public readonly payload: T
    ) { }
}

// Event management class
export class EventManager {
    private static logger: KDAPLogger = new KDAPLogger(EventManager.name);
    private static emitter: EventEmitter = new EventEmitter();

    /**
     * Emits an event with a specified class and payload.
     * @param eventClass - The class of the event to emit.
     * @param payload - The payload to pass to the event.
     */
    public static emit<T>(
        event: CustomEvent<T>): void {
        this.emitter.emit(event.eventName, event.payload);
    }

    /**
     * Registers a listener for a specific event type.
     * @param eventClass - The class of the event to listen for.
     * @param callback - The function to handle the event's payload.
     */
    public static on<T>(
        eventClass: { new(payload: T): CustomEvent<T> },
        callback: (payload: T) => void
    ): void {
        const eventName = new eventClass(null as unknown as T).eventName;
        this.emitter.addListener(eventName, (payload: T) => {
            let payloadString: string = payload as string;
            try {
                payloadString = JSON.stringify(payload);
            }
            catch (err) {
            }
            this.logger.log({ msg: `${eventName} Dispatched with payload ${payloadString}`, func: this.on });
            callback(payload);
        });
    }

    /**
     * Removes a listener for a specific event type.
     * @param eventClass - The class of the event to stop listening for.
     * @param callback - The function to remove.
     */
    public static off<T>(
        eventClass: { new(payload: T): CustomEvent<T> },
        callback: (payload: T) => void
    ): void {
        const eventName = new eventClass(null as unknown as T).eventName;
        this.emitter.removeListener(eventName, callback);
    }
}