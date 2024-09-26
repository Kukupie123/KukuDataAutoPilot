package dev.kukukodes.kdap.dataBoxService.entity.dataEntry;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataEntryValue implements Serializable {
    @Field(DbConst.DocumentFields.DataEntry.DataEntryValue.FIELD_NAME)
    private String fieldName;
    @Field(DbConst.DocumentFields.DataEntry.VALUE)
    private String value;
}
