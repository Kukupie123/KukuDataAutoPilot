package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.service.DataBoxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Endpoints:
 * <p>
 * GET '/'
 *      - Get all databoxes (Admin only).
 * <p>
 * GET '/?user={userId}'
 *      - Get all databoxes for the specified user.
 *      - Accessing another user's databoxes is forbidden unless the requester is an Admin.
 * <p>
 * GET '/{databoxId}'
 *      - Get details of the specified databox by its ID.
 *      - Accessing another user's databox is forbidden unless the requester is an Admin.
 * <p>
 * POST '/{userId}'
 *      - Payload: {@link DataBox}
 *      - Add a new databox for the specified user.
 *      - Creating databoxes for other users is forbidden unless the requester is an Admin.
 * <p>
 * DELETE '/{databoxId}'
 *      - Delete the specified databox by its ID.
 *      - Deletion of another user's databox is forbidden unless the requester is an Admin.
 * <p>
 * UPDATE '/'
 *      - Payload: {@link DataBox}
 *      - Update an existing databox.
 *      - Updating another user's databox is forbidden unless the requester is an Admin.
 */
@Slf4j
@RestController
@RequestMapping("/api/authenticated/databox")
public class DataboxController {

    private final DataBoxService dataBoxService;

    public DataboxController(DataBoxService dataBoxService) {
        this.dataBoxService = dataBoxService;
    }

    /**
     * Get databox list of specified user. Access will be denied if attempting to access dbs of other user without admin role
     * Pass userID as 'user' query param
     *
     * @return list of databox
     */
    @GetMapping("/")
    public ResponseEntity<List<DataBox>> getAllDataBoxes(@RequestParam String user) {
        if(user == null) {
            log.error("No userID found in query param");
            return ResponseEntity.badRequest().build();
        }
        log.info("getting all data boxes for user : {}", user);
        return ResponseEntity.ok(dataBoxService.getDataboxOfUser(user));
    }

    /**
     * Get databox. Access will be denied if attempting to access db of another user without admin role
     * @param id id of the databox
     * @return databox
     */
    @GetMapping("/{id}")
    public ResponseEntity<DataBox> getDataBoxById(@PathVariable("id") String id) {
        log.info("getting data box by id : {}", id);
        try{
            var db = dataBoxService.getDatabox(id);
            return ResponseEntity.ok(db);
        }
        catch (AccessDeniedException e){
            return ResponseEntity.status(403).body(null);
        }
        catch (FileNotFoundException e)
        {
            return ResponseEntity.status(404).body(null);
        }
    }
}
