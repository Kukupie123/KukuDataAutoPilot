package dev.kukukodes.kdap.datastoreService.entity.dataEntry;

import dev.kukukodes.kdap.datastoreService.constants.DbConst;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
public class DataEntryValue {
    @Field(DbConst.DocumentFields.DataEntry.DataEntryValue.FIELD_NAME)
    private String fieldName;
    @Field(DbConst.DocumentFields.DataEntry.VALUE)
    private String value;
}
