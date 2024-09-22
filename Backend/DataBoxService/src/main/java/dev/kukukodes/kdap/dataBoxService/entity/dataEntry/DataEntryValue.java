package dev.kukukodes.kdap.dataBoxService.entity.dataEntry;

import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataEntryValue {
    @Field(DbConst.DocumentFields.DataEntry.DataEntryValue.FIELD_NAME)
    private String fieldName;
    @Field(DbConst.DocumentFields.DataEntry.VALUE)
    private String value;
}
