package com.tcs.JwtLogin.service;

import com.tcs.JwtLogin.dto.*;
import com.tcs.JwtLogin.models.Amenities;
import com.tcs.JwtLogin.models.Hall;
import com.tcs.JwtLogin.models.Seat;
import com.tcs.JwtLogin.models.User;
import com.tcs.JwtLogin.repository.HallRepository;
import com.tcs.JwtLogin.repository.UserRepository;
import lombok.Builder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@Service
public class HallService {

    private final HallRepository hallRepository;
    private final UserRepository userRepository;

    public HallService(HallRepository hallRepository,
                       UserRepository userRepository) {
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
    }

    public HallResponse createHall(HallRequest request) {

        // Get logged-in user from JWT
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        // Build Hall
        Hall hall = new Hall();
        hall.setName(request.getName());
        hall.setPlace(request.getPlace());
        hall.setOpenFrom(request.getOpenFrom());
        hall.setOpenTo(request.getOpenTo());
        hall.setRows(request.getRows());
        hall.setColumns(request.getColumns());
        hall.setAmenities(request.getAmenities());

        // SET OWNER (IMPORTANT)
        hall.setOwner(user);

        hall.setSeats(
                request.getSeats().stream().map(sr -> {
                    Seat seat = new Seat();
                    seat.setRowNumber(sr.getRow());
                    seat.setColumnNumber(sr.getColumn());
                    seat.setActive(sr.isActive());
                    seat.setSeatName(sr.getSeatName());
                    seat.setHall(hall);
                    return seat;
                }).collect(Collectors.toList())
        );

        Hall hall1 = hallRepository.save(hall);
        HallResponse hallResponse = new HallResponse();
        hallResponse.setName(hall1.getName());
        hallResponse.setPlace(hall1.getPlace());
        hallResponse.setRows(hall1.getRows());
        hallResponse.setColumns(hall1.getColumns());
        hallResponse.setOpenTo(hall1.getOpenTo());
        hallResponse.setOpenFrom(hall1.getOpenFrom());
        AmenitiesResponse ar = new AmenitiesResponse();
        ar.setAc(hall.getAmenities().isAc());
        ar.setWheelchairAccess(hall.getAmenities().isWheelchairAccess());
        ar.setParking(hall.getAmenities().isParking());
        ar.setWashroom(hall.getAmenities().isWashroom());
        hallResponse.setAmenities(ar);
        List<SeatResponse> seatResponses = getResponses(hall1);
        hallResponse.setSeats(seatResponses);
        return hallResponse;
    }

    public void deleteHall(Long hallId){
        hallRepository.deleteById(hallId);
    }

    public List<HallResponse> getMyHalls() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return hallRepository.findByOwner(user)
                .stream()
                .map(hall -> {
                    HallResponse res = new HallResponse();
                    res.setId(hall.getId());
                    res.setName(hall.getName());
                    res.setPlace(hall.getPlace());
                    res.setOpenFrom(hall.getOpenFrom());
                    res.setOpenTo(hall.getOpenTo());
                    res.setRows(hall.getRows());
                    res.setColumns(hall.getColumns());
                    AmenitiesResponse ar = new AmenitiesResponse();
                    ar.setAc(hall.getAmenities().isAc());
                    ar.setWheelchairAccess(hall.getAmenities().isWheelchairAccess());
                    ar.setParking(hall.getAmenities().isParking());
                    ar.setWashroom(hall.getAmenities().isWashroom());
                    res.setAmenities(ar);

                    List<SeatResponse> seatResponses = getResponses(hall);
                    res.setSeats(seatResponses);
                    return res;
                })
                .toList();
    }

    public HallResponse getHallById(Long hallId) {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();
        Hall hall = hallRepository.findById(hallId).orElseThrow(()-> new RuntimeException("Hall Not found"));
        User owner = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not found"));
        if(hall.getOwner()==owner) {
            HallResponse hallResponse = new HallResponse();
            hallResponse.setName(hall.getName());
            hallResponse.setId(hallId);
            hallResponse.setPlace(hall.getPlace());
            hallResponse.setOpenTo(hall.getOpenTo());
            hallResponse.setOpenFrom(hall.getOpenFrom());
            Amenities amenities = hall.getAmenities();
            AmenitiesResponse amenitiesResponse = new AmenitiesResponse();
            amenitiesResponse.setAc(hall.getAmenities().isAc());
            amenitiesResponse.setWheelchairAccess(hall.getAmenities().isWheelchairAccess());
            amenitiesResponse.setWashroom(hall.getAmenities().isWashroom());
            amenitiesResponse.setParking(hall.getAmenities().isParking());
            hallResponse.setAmenities(amenitiesResponse);
            hallResponse.setRows(hall.getRows());
            hallResponse.setColumns(hall.getColumns());
            List<SeatResponse> seatResponses = getResponses(hall);

            hallResponse.setSeats(seatResponses);

            return hallResponse;
        } else
            throw new RuntimeException("You can only view your hall");

    }

    public HallResponse updateHall(Long hallId, HallRequest hallRequest) {

        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new NoSuchElementException("Hall not found"));

        // ---- Update hall fields ----
        hall.setName(hallRequest.getName());
        hall.setPlace(hallRequest.getPlace());
        hall.setRows(hallRequest.getRows());
        hall.setColumns(hallRequest.getColumns());
        hall.setOpenFrom(hallRequest.getOpenFrom());
        hall.setOpenTo(hallRequest.getOpenTo());
        hall.setAmenities(hallRequest.getAmenities());

        // ---- Existing seats from DB ----
        Map<Long, Seat> existingSeatMap = hall.getSeats().stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));

        List<Seat> updatedSeats = new ArrayList<>();

        for (SeatRequest seatRequest : hallRequest.getSeats()) {

            Seat seat;

            if (seatRequest.getId() != null &&
                    existingSeatMap.containsKey(seatRequest.getId())) {

                // ✅ UPDATE existing seat
                seat = existingSeatMap.get(seatRequest.getId());

            } else {
                // ✅ NEW seat (rare case)
                seat = new Seat();
                seat.setHall(hall);
            }

            seat.setRowNumber(seatRequest.getRow());
            seat.setColumnNumber(seatRequest.getColumn());
            seat.setActive(seatRequest.isActive());
            seat.setSeatName(seatRequest.getSeatName());

            updatedSeats.add(seat);
        }

        // ---- Replace seats safely ----
        hall.getSeats().clear();
        hall.getSeats().addAll(updatedSeats);

        Hall savedHall = hallRepository.save(hall);

        return mapToHallResponse(savedHall);
    }
    private HallResponse mapToHallResponse(Hall hall) {

        HallResponse response = new HallResponse();
        response.setId(hall.getId());
        response.setName(hall.getName());
        response.setPlace(hall.getPlace());
        response.setRows(hall.getRows());
        response.setColumns(hall.getColumns());
        response.setOpenFrom(hall.getOpenFrom());
        response.setOpenTo(hall.getOpenTo());

        // ---- Amenities ----
        AmenitiesResponse amenitiesResponse = new AmenitiesResponse();
        amenitiesResponse.setAc(hall.getAmenities().isAc());
        amenitiesResponse.setParking(hall.getAmenities().isParking());
        amenitiesResponse.setWashroom(hall.getAmenities().isWashroom());
        amenitiesResponse.setWheelchairAccess(
                hall.getAmenities().isWheelchairAccess()
        );
        response.setAmenities(amenitiesResponse);

        // ---- Seats ----
        List<SeatResponse> seatResponses = hall.getSeats()
                .stream()
                .map(seat -> {
                    SeatResponse sr = new SeatResponse();
                    sr.setId(seat.getId());
                    sr.setRow(seat.getRowNumber());
                    sr.setColumn(seat.getColumnNumber());
                    sr.setActive(seat.isActive());
                    sr.setSeatName(seat.getSeatName());
                    return sr;
                })
                .toList();

        response.setSeats(seatResponses);

        return response;
    }


    private static List<SeatResponse> getResponses(Hall hall) {
        List<Seat> seats = hall.getSeats();
        List<SeatResponse> seatResponses = new ArrayList<>();
        for (Seat seat : seats) {
            SeatResponse seatResponse = new SeatResponse();
            seatResponse.setId(seat.getId());
            seatResponse.setSeatName(seat.getSeatName());
            seatResponse.setActive(seat.isActive());
            seatResponse.setRow(seat.getRowNumber());
            seatResponse.setColumn(seat.getColumnNumber());
            seatResponses.add(seatResponse);
        }
        return seatResponses;
    }

}
