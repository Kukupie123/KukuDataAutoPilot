package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBoxField;
import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;


class DataEntryHelperTest {
    private final DataEntryHelper helper = new DataEntryHelper();
    private DataBox testDataBox;
    private List<DataBoxField> testFields;
    private DataEntry testEntry;

    @BeforeEach
    void setUp() {
        testFields = new ArrayList<>();
        testFields.add(new DataBoxField("name", DataBoxFieldType.TEXT, true));
        testFields.add(new DataBoxField("dob", DataBoxFieldType.DATE, true));
        testFields.add(new DataBoxField("tired", DataBoxFieldType.TRUE_FALSE, false));

        testDataBox = new DataBox("testUser", "testName", "desc", testFields);
    }

    @AfterEach
    void tearDown() {
        testFields.clear();
        testDataBox = null;

    }

    @Test
    void basicTest() {
        Map<String, String> entries = new HashMap<>();
        entries.put("name", "testName");
        entries.put("dob", LocalDate.now().toString());
        entries.put("tired", "TRUE");

        var canAdd = helper.validateEntryForDataBox(testDataBox, new DataEntry(testDataBox.getId(), entries));
        Assertions.assertThat(canAdd).isTrue();
    }
    @Test
    void tooManyEntriesTest(){
        Map<String, String> entries = new HashMap<>();
        entries.put("name", "testName");
        entries.put("dob", LocalDate.now().toString());
        entries.put("tired", "TRUE");
        entries.put("extra","extra");
        var canAdd = helper.validateEntryForDataBox(testDataBox, new DataEntry(testDataBox.getId(), entries));
        Assertions.assertThat(canAdd).isFalse();
    }
    @Test
    void skippingOptionalTest(){
        Map<String, String> entries = new HashMap<>();
        entries.put("name", "testName");
        entries.put("dob", LocalDate.now().toString());
        var canAdd = helper.validateEntryForDataBox(testDataBox, new DataEntry(testDataBox.getId(), entries));
        Assertions.assertThat(canAdd).isTrue();
    }

    @Test
    void skippingNonOptionalTest(){
        Map<String, String> entries = new HashMap<>();
        entries.put("dob", LocalDate.now().toString());
        entries.put("tired", "TRUE");
        var canAdd = helper.validateEntryForDataBox(testDataBox, new DataEntry(testDataBox.getId(), entries));
        Assertions.assertThat(canAdd).isFalse();
    }

    @Test
    void invalidFieldValueTest(){
        Map<String, String> entries = new HashMap<>();
        entries.put("name", "testName");
        entries.put("dob", LocalDate.now().toString());
        entries.put("tired", "yes i am");

        var canAdd = helper.validateEntryForDataBox(testDataBox, new DataEntry(testDataBox.getId(), entries));
        Assertions.assertThat(canAdd).isFalse();
    }

}
