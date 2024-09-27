package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.helper.LogHelper;
import dev.kukukodes.kdap.dataBoxService.helper.SecurityHelper;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.service.DataBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for handling operations related to DataBoxes.
 * <p>
 * Endpoints:
 * - GET /api/authenticated/databox/?boxid=&userid= : Retrieves DataBox(es) based on query params.
 * - If 'boxid' is provided, fetches the specific DataBox.
 * - If 'userid' is provided, fetches all DataBoxes of that user.
 * - Otherwise, returns all DataBoxes with optional pagination (skip and limit).
 * <p>
 * - POST /api/authenticated/databox/ : Adds a new DataBox. The DataBox payload should be in the request body.
 * <p>
 * - PUT /api/authenticated/databox/ : Updates an existing DataBox. The DataBox to be updated should be in the request body.
 * <p>
 * - DELETE /api/authenticated/databox/{id} : Deletes the specified DataBox by its ID.
 */
@Slf4j
@RestController
@RequestMapping("/api/authenticated/databox")
@RequiredArgsConstructor
public class DataboxController {

    private final SecurityHelper securityHelper;
    private final DataBoxService dataBoxService;
    private final LogHelper logHelper;

    /**
     * Fetches DataBox(es) based on query parameters.
     *
     * @param boxid  (Optional) If provided, returns the DataBox with this ID.
     * @param userid (Optional) If provided, returns all DataBoxes for this user.
     * @param skip   (Optional) The number of records to skip for pagination. Default is 0.
     * @param limit  (Optional) The maximum number of records to return. Default is 10.
     * @return Response containing the DataBox(es).
     */
    @GetMapping("/")
    public ResponseEntity<ResponseModel<List<DataBox>>> getDataboxes(@RequestParam(required = false) String boxid,
                                                                     @RequestParam(required = false) String userid,
                                                                     @RequestParam(defaultValue = "0") int skip,
                                                                     @RequestParam(defaultValue = "10") int limit) {
        try {
            if (boxid != null && !boxid.isEmpty()) {
                log.info("Fetching data for box ID: {}", boxid);
                return ResponseModel.success("Box data retrieved", List.of(dataBoxService.getDatabox(boxid)));
            } else if (userid != null && !userid.isEmpty()) {
                log.info("Fetching databoxes for user ID: {}", userid);
                return ResponseModel.success("User's databoxes retrieved", dataBoxService.getDataboxesOfUser(userid));
            } else {
                log.info("Fetching all databoxes with pagination - skip: {}, limit: {}", skip, limit);
                return ResponseModel.success("All databoxes retrieved", dataBoxService.getAllDatabox(skip, limit));
            }
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }

    /**
     * Adds a new DataBox.
     *
     * @param dataBox The DataBox object to be created.
     * @return Response containing the newly created DataBox.
     */
    @PostMapping("/")
    public ResponseEntity<ResponseModel<DataBox>> createDatabox(@RequestBody DataBox dataBox) {
        try {
            log.info("Creating new databox: {}", dataBox);
            return ResponseModel.success("Databox created successfully", dataBoxService.addDatabox(dataBox));
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage() + "\n Because " + e.getCause() + "\n Stack : " + Arrays.toString(e.getStackTrace()), null, 500);
        }
    }

    /**
     * Updates an existing DataBox.
     *
     * @param dataBox The DataBox object containing updated data.
     * @return Response indicating success or failure.
     */
    @PutMapping("/")
    public ResponseEntity<ResponseModel<Boolean>> updateDatabox(@RequestBody DataBox dataBox) {
        log.info("Updating databox: {}", dataBox);
        try {
            return ResponseModel.success("Databox updated successfully", dataBoxService.updateDatabox(dataBox));
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }

    /**
     * Deletes a DataBox by its ID.
     *
     * @param id The ID of the DataBox to be deleted.
     * @return Response indicating success or failure.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Boolean>> deleteDatabox(@PathVariable("id") String id) {
        log.info("Deleting databox with ID: {}", id);
        try {
            return ResponseModel.success("Databox deleted successfully", dataBoxService.deleteDatabox(id));
        } catch (Exception e) {
            logHelper.logException(log, e);
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }
}
