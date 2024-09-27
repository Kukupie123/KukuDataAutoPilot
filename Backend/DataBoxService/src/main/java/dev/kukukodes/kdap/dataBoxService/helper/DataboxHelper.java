package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataboxHelper {
    /**
     * Validates if the fields of databox are valid.
     * Does not validate userID!
     */
    public void validateDataboxValues(DataBox dataBox) throws Exception {
        if (dataBox == null) throw new NullPointerException("Databox is null");
        if (dataBox.getFields() == null || dataBox.getFields().isEmpty())
            throw new Exception("Missing fields for " + dataBox);
        if (dataBox.getName() == null || dataBox.getName().isEmpty())
            throw new Exception("Missing name for databox " + dataBox);
    }

    /**
     * @return true if both DataBoxes are similar.
     */
    public boolean compareDataboxes(DataBox dataBox1, DataBox dataBox2) {
        if (dataBox1 == dataBox2) return true;

        // Basic field comparisons
        if (!dataBox1.getId().equals(dataBox2.getId())) return false;
        if (!dataBox1.getName().equals(dataBox2.getName())) return false;
        if (!dataBox1.getUserID().equals(dataBox2.getUserID())) return false;
        if (!dataBox1.getModified().equals(dataBox2.getModified())) return false;
        if (!dataBox1.getCreated().equals(dataBox2.getCreated())) return false;
        if (!dataBox1.getDescription().equals(dataBox2.getDescription())) return false;

        // Check if field sizes are equal
        if (dataBox1.getFields().size() != dataBox2.getFields().size()) return false;

        // Compare fields by key-value pairs
        for (var entry : dataBox1.getFields().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            // Check if dataBox2 has the same key and value
            if (!dataBox2.getFields().containsKey(key) || !dataBox2.getFields().get(key).equals(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param original the original databox
     * @param updated the updated databox
     * @return original databox with updated values
     */
    public DataBox updateDataboxFrom(DataBox original, DataBox updated) {
        if (!original.getCreated().equals(updated.getCreated())) original.setCreated(updated.getCreated());
        if (!original.getModified().equals(updated.getModified())) original.setModified(updated.getModified());
        if (!original.getDescription().equals(updated.getDescription()))
            original.setDescription(updated.getDescription());
        if (original.getFields().size() != updated.getFields().size()) {
            original.getFields().clear();
            original.getFields().putAll(updated.getFields());
        }
        if (!original.getUserID().equals(updated.getUserID())) original.setUserID(updated.getUserID());
        return original;
    }

}
