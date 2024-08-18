import { EventEmitter } from "events";  // Use 'events' instead of 'stream' for Node.js EventEmitter

// Base class for custom events
export abstract class CustomEvent<T> {
    constructor(
        public readonly eventName: string,
        public readonly payload: T
    ) { }
}

// Example event extending CustomEvent
export class TestEvent extends CustomEvent<string> {
    constructor(payload: string) {
        super('TestEvent', payload);
    }
}

// Event management class
export class EventManager {
    private static emitter: EventEmitter = new EventEmitter();

    /**
     * Emits an event with a specified class and payload.
     * @param eventClass - The class of the event to emit.
     * @param payload - The payload to pass to the event.
     */
    public static emit<T>(
        eventClass: { new(payload: T): CustomEvent<T> },
        payload: T
    ): void {
        const eventInstance = new eventClass(payload);
        this.emitter.emit(eventInstance.eventName, eventInstance.payload);
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
