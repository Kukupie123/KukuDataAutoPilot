package dev.kukukodes.kdap.datastoreService.entity.dataEntry;

import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

public class DataEntryValue {
    @MongoId(FieldType.STRING)
    private String fieldName;
    private String value;
}
