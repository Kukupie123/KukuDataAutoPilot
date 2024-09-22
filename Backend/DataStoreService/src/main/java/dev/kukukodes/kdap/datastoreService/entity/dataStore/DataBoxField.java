package dev.kukukodes.kdap.datastoreService.entity.dataStore;


import dev.kukukodes.kdap.datastoreService.constants.DbConst;
import dev.kukukodes.kdap.datastoreService.enums.DataStoreFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBoxField {
    @Field(DbConst.DocumentFields.DataBox.DataBoxField.FIELD_NAME)
    private String fieldName;
    @Field(DbConst.DocumentFields.DataBox.DataBoxField.FIELD_TYPE)
    private DataStoreFieldType fieldType;
    @Field(DbConst.DocumentFields.DataBox.DataBoxField.REQUIRED)
    private boolean required;
}
