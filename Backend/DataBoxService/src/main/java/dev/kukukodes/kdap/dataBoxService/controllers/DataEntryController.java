package dev.kukukodes.kdap.dataBoxService.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Possible actions
 * 1. get all data entry with skip and limit based on databox id
 * 2. get specific data entry by boxid and  data entry id
 * 3. add data entry to databox
 * 4. update data entry
 * 5. delete data entry
 */
@RestController
@RequestMapping("/api/authenticated/dataentry")
public class DataEntryController {
}
