package com.tcs.JwtLogin.service;

import com.tcs.JwtLogin.dto.*;
import com.tcs.JwtLogin.models.*;
import com.tcs.JwtLogin.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizerEventService {

    private final EventRepository eventRepository;
    private final HallRepository hallRepository;
    private final OrganizerSeatRepository organizerSeatRepository;
    private final UserRepository userRepository;

    public OrganizerEventService(
            EventRepository eventRepository,
            HallRepository hallRepository,
            OrganizerSeatRepository organizerSeatRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.hallRepository = hallRepository;
        this.organizerSeatRepository = organizerSeatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {

        // 1️⃣ Prevent double booking
        if (eventRepository.existsByHallIdAndEventDate(
                request.getHallId(), request.getEventDate())) {
            throw new IllegalStateException("Hall already booked for this date");
        }

        // 2️⃣ Fetch Hall
        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new EntityNotFoundException("Hall not found"));

        // 3️⃣ Fetch Organizer from JWT
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 4️⃣ Create Event
        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setEventStartTime(request.getStartTime());
        event.setEventEndTime(request.getEndTime());
        event.setRows(hall.getRows());
        event.setColumns(hall.getColumns());
        event.setHall(hall);
        event.setAddedBy(organizer);

        eventRepository.save(event);

        // 5️⃣ Clone Hall seats → Organizer seats
        List<OrganizerSeat> organizerSeats = new ArrayList<>();

        for (Seat hallSeat : hall.getSeats()) {

            SeatType seatType = SeatType.NORMAL;

            if (request.getSeatOverrides() != null) {
                for (OrganizerSeatRequest override : request.getSeatOverrides()) {
                    if (override.getRow().equals(hallSeat.getRowNumber())
                            && override.getColumn().equals(hallSeat.getColumnNumber())
                            && override.getSeatType() != null) {
                        seatType = override.getSeatType();
                        break;
                    }
                }
            }

            OrganizerSeat os = new OrganizerSeat();
            os.setEvent(event);
            os.setRow(hallSeat.getRowNumber());
            os.setColumn(hallSeat.getColumnNumber());
            os.setSeatName(hallSeat.getSeatName());
            os.setActive(hallSeat.isActive());
            os.setSeatType(seatType);

            organizerSeats.add(os);
        }

        organizerSeatRepository.saveAll(organizerSeats);
        event.setSeats(organizerSeats);

        // 6️⃣ Return DTO
        return toResponse(event);
    }

    // ===================== MAPPER =====================

    private EventResponse toResponse(Event event) {

        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setEventName(event.getEventName());
        response.setEventDate(event.getEventDate());
        response.setStartTime(event.getEventStartTime());
        response.setEndTime(event.getEventEndTime());
        response.setRows(event.getRows());
        response.setColumns(event.getColumns());
        response.setHallId(event.getHall().getId());
        response.setHallName(event.getHall().getName());

        List<OrganizerSeatResponse> seatResponses = new ArrayList<>();

        for (OrganizerSeat seat : event.getSeats()) {
            OrganizerSeatResponse dto = new OrganizerSeatResponse();
            dto.setRow(seat.getRow());
            dto.setColumn(seat.getColumn());
            dto.setSeatName(seat.getSeatName());
            dto.setActive(seat.isActive());
            dto.setSeatType(seat.getSeatType());
            seatResponses.add(dto);
        }

        response.setSeats(seatResponses);
        return response;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEventsForOrganizer() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Event> events =
                eventRepository.findAllByOrganizerWithSeats(organizer.getId());

        return events.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {

        // Get logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Fetch event with seats (ownership enforced)
        Event event = eventRepository
                .findByIdAndOrganizerWithSeats(eventId, organizer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        return toResponse(event);
    }
    @Transactional
    public void deleteEvent(Long eventId) {

        // 1️⃣ Get logged-in organizer
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2️⃣ Fetch event (ownership enforced)
        Event event = eventRepository
                .findByIdAndAddedById(eventId, organizer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // 3️⃣ Delete event (seats auto-deleted)
        eventRepository.delete(event);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, UpdateEventRequest request) {

        // 1️⃣ Get logged-in organizer
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2️⃣ Fetch event (ownership enforced)
        Event event = eventRepository
                .findByIdAndAddedById(eventId, organizer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // 3️⃣ Update event basic info
        if (request.getEventName() != null) {
            event.setEventName(request.getEventName());
        }

        if (request.getEventDate() != null) {
            // Optional: check hall availability again
            if (!event.getEventDate().equals(request.getEventDate()) &&
                    eventRepository.existsByHallIdAndEventDate(
                            event.getHall().getId(), request.getEventDate())) {
                throw new IllegalStateException("Hall already booked for this date");
            }
            event.setEventDate(request.getEventDate());
        }

        if (request.getStartTime() != null) {
            event.setEventStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            event.setEventEndTime(request.getEndTime());
        }

        // 4️⃣ Update seat types (ONLY seatType)
        if (request.getSeatOverrides() != null) {
            for (OrganizerSeat seat : event.getSeats()) {
                for (OrganizerSeatRequest override : request.getSeatOverrides()) {
                    if (override.getRow().equals(seat.getRow())
                            && override.getColumn().equals(seat.getColumn())
                            && override.getSeatType() != null) {
                        seat.setSeatType(override.getSeatType());
                    }
                }
            }
        }

        // Event & seats are managed entities → auto-flush
        return toResponse(event);
    }

    public List<HallResponse> getMyHalls() {
        return hallRepository.findAll()
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

        Hall hall = hallRepository.findById(hallId).orElseThrow(()-> new RuntimeException("Hall Not found"));
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
