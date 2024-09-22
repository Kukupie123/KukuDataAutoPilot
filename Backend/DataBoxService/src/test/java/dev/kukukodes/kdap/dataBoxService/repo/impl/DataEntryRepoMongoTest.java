package dev.kukukodes.kdap.dataBoxService.repo.impl;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBoxField;
import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import dev.kukukodes.kdap.dataBoxService.helper.DataEntryHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class DataEntryRepoMongoTest {

    List<DataBoxField> testDataBoxFields = new ArrayList<>();
    DataBox testDataBox;
    @Autowired
    private MongoTemplate mongoTemplate;
    private DataBoxRepoMongo dataBoxRepoMongo;
    private DataEntryRepoMongo dataEntryRepoMongo;

    @BeforeEach
    void setUp() {
        dataBoxRepoMongo = new DataBoxRepoMongo(mongoTemplate);
        dataEntryRepoMongo = new DataEntryRepoMongo(mongoTemplate, new DataEntryHelper(), dataBoxRepoMongo);
        testDataBoxFields.add(new DataBoxField("name", DataBoxFieldType.TEXT, true));
        testDataBoxFields.add(new DataBoxField("dob", DataBoxFieldType.DATE, true));
        testDataBoxFields.add(new DataBoxField("nickname", DataBoxFieldType.TEXT, false));

        testDataBox = new DataBox("testUserID", "testDataBox", "test desc", testDataBoxFields);
        dataBoxRepoMongo.addDataStore(testDataBox);
    }

    @AfterEach
    void tearDown() {
        testDataBox = null;
        testDataBoxFields.clear();
        mongoTemplate.remove(new Query(), DataBox.class);
        mongoTemplate.remove(new Query(), DataEntry.class);
    }

    @Test
    void addDateEntry() {
        //basic
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("name", "test name");
        entryMap.put("dob", LocalDate.now().toString());
        entryMap.put("nickname", "test nickname");
        var entry = new DataEntry(testDataBox.getId(), entryMap);
        var added = dataEntryRepoMongo.addDateEntry(entry);
        Assertions.assertThat(added).isNotNull();
        Assertions.assertThat(added.getBoxID()).isEqualTo(testDataBox.getId());

    }

    @Test
    void addDataSkipOptional() {
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("name", "test name");
        entryMap.put("dob", LocalDate.now().toString());
        var entry = new DataEntry(testDataBox.getId(), entryMap);
        var added = dataEntryRepoMongo.addDateEntry(entry);
        Assertions.assertThat(added).isNotNull();
        Assertions.assertThat(added.getBoxID()).isEqualTo(testDataBox.getId());
        Assertions.assertThat(added.getValues().get("nickname")).isNull();
    }

    @Test
    void updateDateEntry() {
        //add first
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("name", "test name");
        entryMap.put("dob", LocalDate.now().toString());
        var entry = new DataEntry(testDataBox.getId(), entryMap);
        var added = dataEntryRepoMongo.addDateEntry(entry);
        Assertions.assertThat(added).isNotNull();
        Assertions.assertThat(added.getBoxID()).isEqualTo(testDataBox.getId());
        Assertions.assertThat(added.getValues().get("nickname")).isNull();

        //update test
        added.getValues().put("nickname", "test nickname");
        var updatedEntry = dataEntryRepoMongo.updateDateEntry(entry);
        Assertions.assertThat(added.getValues().get("nickname")).isEqualTo("test nickname");
    }

    @Test
    void deleteDateEntry() {
        //add first
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("name", "test name");
        entryMap.put("dob", LocalDate.now().toString());
        var entry = new DataEntry(testDataBox.getId(), entryMap);
        var added = dataEntryRepoMongo.addDateEntry(entry);
        Assertions.assertThat(added).isNotNull();
        Assertions.assertThat(added.getBoxID()).isEqualTo(testDataBox.getId());
        Assertions.assertThat(added.getValues().get("nickname")).isNull();

        var deleted = dataEntryRepoMongo.deleteDateEntry(added);
        Assertions.assertThat(deleted).isTrue();
        var found = dataEntryRepoMongo.getDataEntry(added.getId());
        Assertions.assertThat(found).isNull();
    }

    @Test
    void getDataEntry() {
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("name", "test name");
        entryMap.put("dob", LocalDate.now().toString());
        var entry = new DataEntry(testDataBox.getId(), entryMap);
        var added = dataEntryRepoMongo.addDateEntry(entry);
        Assertions.assertThat(added).isNotNull();
        Assertions.assertThat(added.getBoxID()).isEqualTo(testDataBox.getId());
        Assertions.assertThat(added.getValues().get("nickname")).isNull();
        var gotten = dataEntryRepoMongo.getDataEntry(added.getId());
        Assertions.assertThat(gotten).isNotNull();
        Assertions.assertThat(gotten.getBoxID()).isEqualTo(testDataBox.getId());
        Assertions.assertThat(gotten.getValues().get("nickname")).isNull();

        gotten = dataEntryRepoMongo.getDataEntry("dummy");
        Assertions.assertThat(gotten).isNull();
    }

    @Test
    void getDataEntriesByBoxID() {
        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("name", "test name");
        entryMap.put("dob", LocalDate.now().toString());
        var entry = new DataEntry(testDataBox.getId(), entryMap);
        var added = dataEntryRepoMongo.addDateEntry(entry);
        Assertions.assertThat(added).isNotNull();
        Assertions.assertThat(added.getBoxID()).isEqualTo(testDataBox.getId());
        Assertions.assertThat(added.getValues().get("nickname")).isNull();

        var found = dataEntryRepoMongo.getDataEntriesByBoxID(added.getBoxID());
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.size()).isEqualTo(1);
        Assertions.assertThat(found.get(0).getBoxID()).isEqualTo(testDataBox.getId());

        found = dataEntryRepoMongo.getDataEntriesByBoxID("dunny");
        Assertions.assertThat(found).isEmpty();
    }
}
