package com.tcs.JwtLogin.controller;


import com.tcs.JwtLogin.dto.ApiResponse;
import com.tcs.JwtLogin.dto.HallRequest;
import com.tcs.JwtLogin.dto.HallResponse;
import com.tcs.JwtLogin.models.Hall;
import com.tcs.JwtLogin.service.HallService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hall")
@CrossOrigin
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @PreAuthorize("hasRole('HALL_MANAGER')")
    @PostMapping("/halls")
    public ResponseEntity<HallResponse> createHall(@RequestBody HallRequest request) {
        return ResponseEntity.status(200).body(hallService.createHall(request));
    }

    @DeleteMapping("/halls/{hallId}")
    public ResponseEntity<?> deleteHall(@PathVariable Long hallId){
        hallService.deleteHall(hallId);
        return ResponseEntity.status(200).body("Hall deleted successfully");
    }

    @PreAuthorize("hasRole('HALL_MANAGER')")
    @GetMapping("/my-halls")
    public List<HallResponse> getMyHalls() {
        return hallService.getMyHalls();
    }

    @GetMapping("/my-halls/{hallId}")
    public ResponseEntity<?> getHallById(@PathVariable Long hallId){
        try{
            HallResponse hallResponse = hallService.getHallById(hallId);
            return  ResponseEntity.status(200).body(hallResponse);

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("No Hall found", 500));
        }
    }

    @PutMapping("/my-halls/{hallId}/edit")
    public ResponseEntity<?> updateHall(@PathVariable Long hallId, @RequestBody HallRequest hallRequest){
        try{
            HallResponse hallResponse = hallService.updateHall(hallId, hallRequest);
            return ResponseEntity.ok().body(hallResponse);
        } catch (Exception e){
            return ResponseEntity.status(500).body("Error Updating hall");
        }
    }
}

