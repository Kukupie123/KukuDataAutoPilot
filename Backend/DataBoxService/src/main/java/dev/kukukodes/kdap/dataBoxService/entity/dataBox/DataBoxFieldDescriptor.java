package dev.kukukodes.kdap.dataBoxService.entity.dataBox;

import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataBoxFieldDescriptor implements Serializable {
    private DataBoxFieldType fieldType;
    private boolean required;
}
