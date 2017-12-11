package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.Activity;
import com.dropboxPrototypeBackend.entity.ErrorResponse;
import com.dropboxPrototypeBackend.entity.SuccessResponse;
import com.dropboxPrototypeBackend.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    private List<Activity> activities;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getActivities(@RequestParam(value = "uid") String email) {
        activities = activityRepository.findFirst5ByEmailOrderByCreatedAtDesc(email);
        if (activities != null) {
            return new SuccessResponse(200, "Activities retrieved successfully.", activities);
        } else {
            return new ErrorResponse(500, "Cannot retrieve activities.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Object getAllActivities(@RequestParam(value = "uid") String email) {
        activities = activityRepository.findAllByEmail(email);
        if (activities != null) {
            return new SuccessResponse(200, "Activities retrieved successfully.", activities);
        } else {
            return new ErrorResponse(500, "Cannot retrieve activities.", "Internal server error.");
        }
    }
}
