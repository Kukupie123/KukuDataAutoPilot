import { RecordModel } from "../../models/RecordModel";
import { CustomEvent } from "../EventSystem";

export class RecordAddedEvent extends CustomEvent<RecordModel> {
    constructor(rec: RecordModel) {
        super(RecordAddedEvent.name, rec)
    }
}