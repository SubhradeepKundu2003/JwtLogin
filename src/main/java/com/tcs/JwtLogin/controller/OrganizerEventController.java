package com.tcs.JwtLogin.controller;

import com.tcs.JwtLogin.dto.*;
import com.tcs.JwtLogin.service.HallService;
import com.tcs.JwtLogin.service.OrganizerEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizer/events")
public class OrganizerEventController {

    private final OrganizerEventService organizerEventService;
    private final HallService hallService;

    public OrganizerEventController(OrganizerEventService organizerEventService, HallService hallService) {
        this.organizerEventService = organizerEventService;
        this.hallService=hallService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody CreateEventRequest request
    ) {
        EventResponse response = organizerEventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEventsForOrganizer() {
        return ResponseEntity.ok(
                organizerEventService.getAllEventsForOrganizer()
        );
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                organizerEventService.getEventById(eventId)
        );
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId
    ) {
        organizerEventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @RequestBody UpdateEventRequest request
    ) {
        EventResponse response =
                organizerEventService.updateEvent(eventId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-halls/{hallId}")
    public ResponseEntity<?> getHallById(@PathVariable Long hallId){
        try{
            HallResponse hallResponse = organizerEventService.getHallById(hallId);
            return  ResponseEntity.status(200).body(hallResponse);

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("No Hall found", 500));
        }
    }
    @GetMapping("/my-halls")
    public List<HallResponse> getMyHalls() {
        return organizerEventService.getMyHalls();
    }
}
