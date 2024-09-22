package dev.kukukodes.kdap.datastoreService.entity.dataStore;


import dev.kukukodes.kdap.datastoreService.enums.DataStoreFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBoxField {
    @MongoId(FieldType.STRING)
    private String fieldName;
    private DataStoreFieldType fieldType;
    private boolean required;
}
