package dev.kukukodes.kdap.dataBoxService.entity.dataBox;


import com.fasterxml.jackson.annotation.JsonInclude;
import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DataBoxField implements  Serializable {
    @Field(DbConst.DocumentFields.DataBox.DataBoxField.FIELD_NAME)
    private String fieldName;
    @Field(DbConst.DocumentFields.DataBox.DataBoxField.FIELD_TYPE)
    private DataBoxFieldType fieldType;
    @Field(DbConst.DocumentFields.DataBox.DataBoxField.REQUIRED)
    private boolean required;
}
