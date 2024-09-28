package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.DataEntryNotFound;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.InvalidFieldValue;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.MissingField;
import dev.kukukodes.kdap.dataBoxService.exceptions.dataentry.WrongNumberOfFields;
import dev.kukukodes.kdap.dataBoxService.helper.ExceptionHelper;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.service.DataEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
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
    private final ExceptionHelper exceptionHelper;

    @GetMapping("/")
    public ResponseEntity<ResponseModel<List<DataEntry>>> getDataEntries(@RequestParam(required = false) String boxid,
                                                                         @RequestParam(required = false) String dataentryid,
                                                                         @RequestParam(required = false, defaultValue = "0") int skip,
                                                                         @RequestParam(required = false, defaultValue = "10") int limit) throws AccessDeniedException, FileNotFoundException {

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

    }

    @PostMapping("/")
    public ResponseEntity<ResponseModel<DataEntry>> addDataEntry(@RequestBody DataEntry dataEntry) throws AccessDeniedException, FileNotFoundException, InvalidFieldValue, MissingField, WrongNumberOfFields {
        return ResponseModel.success("", dataEntryService.addDataEntryForBox(dataEntry));

    }

    @PutMapping("/")
    public ResponseEntity<ResponseModel<Boolean>> updateDataEntry(@RequestBody DataEntry dataEntry) throws AccessDeniedException, FileNotFoundException, InvalidFieldValue, MissingField, WrongNumberOfFields {
        return ResponseModel.success("", dataEntryService.updateDataEntryForBox(dataEntry));

    }

    //TODO: caching and publishing event
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Boolean>> deleteDataEntry(@PathVariable String id) throws AccessDeniedException, FileNotFoundException, DataEntryNotFound {
        return ResponseModel.success("", dataEntryService.deleteDataEntryForBox(id));

    }

    @ExceptionHandler
    public ResponseEntity<ResponseModel<Object>> handleException(Exception ex) {
        return exceptionHelper.kdapExceptionHandler(ex);
    }
}
