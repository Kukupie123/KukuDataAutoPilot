package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.helper.LogHelper;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.service.DataEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Possible actions
 * 1. get all data entry with skip and limit based on databox id
 * 2. get specific data entry by boxid and  data entry id
 * 3. add data entry to databox
 * 4. update data entry
 * 5. delete data entry
 */
@Slf4j
@RestController
@RequestMapping("/api/authenticated/dataentry")
@RequiredArgsConstructor
public class DataEntryController {

    private final DataEntryService dataEntryService;
    private final LogHelper logHelper;

    @GetMapping("/")
    public ResponseEntity<ResponseModel<List<DataEntry>>> getDataEntries(@RequestParam(required = false) String boxid,
                                                                         @RequestParam(required = false) String dataentryid,
                                                                         @RequestParam(required = false, defaultValue = "0") int skip,
                                                                         @RequestParam(required = false, defaultValue = "10") int limit) {

        try {
            if (boxid != null && !boxid.isEmpty()) {
                log.info("Getting all entries of box {}", boxid);
                var boxes = dataEntryService.getDataEntriesByBoxID(boxid, skip, limit);
                return ResponseModel.success("", boxes);
            }
            if (dataentryid != null && !dataentryid.isEmpty()) {
                log.info("Getting dataentry with id {}", dataentryid);
                return ResponseModel.success("", Collections.singletonList(dataEntryService.getDataEntry(dataentryid)));
            }
            return ResponseModel.buildResponse("missing boxid and/or dataentryid with optional skip and limit query parameter", null, HttpStatus.SC_BAD_REQUEST);
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }

    @PostMapping("/")
    public ResponseEntity<ResponseModel<DataEntry>> addDataEntry(@RequestBody DataEntry dataEntry) {
        try {
            return ResponseModel.success("", dataEntryService.addDataEntryForBox(dataEntry));
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }

    @PutMapping("/")
    public ResponseEntity<ResponseModel<Boolean>> updateDataEntry(@RequestBody DataEntry dataEntry) {
        try {
            return ResponseModel.success("", dataEntryService.updateDataEntryForBox(dataEntry));
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }

    //TODO: DElete, caching and publishing event
}
