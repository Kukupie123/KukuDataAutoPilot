import { CustomEvent, EventManager } from "../../../src/eventSystem/EventSystem";
import { describe, expect, test, beforeEach, afterEach } from '@jest/globals';
import { sleep } from "../../../src/util/Sleep";

describe('Event System', () => {
    beforeEach(() => {
        // Before each test
    });

    afterEach(() => {
        // Clean up after each test
    });

    test('should trigger event and handle payload correctly', async () => {
        class TestEvent extends CustomEvent<string> {
            constructor(payload: string) {
                super("TEST_EVENT", payload);
            }
        }
        const payload = "SUP";
        // Setup event listener
        EventManager.on(TestEvent, (p) => {
            //Assert
            expect(payload).toBe(p);
        });
        // Emit the event
        EventManager.emit(new TestEvent(payload));

    });
});
