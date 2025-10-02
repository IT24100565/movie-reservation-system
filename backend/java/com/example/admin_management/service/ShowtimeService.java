package com.example.admin_management.service;


import com.example.admin_management.model.Showtime;
import com.example.admin_management.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final SystemLogService logService;


    public ShowtimeService(ShowtimeRepository showtimeRepository, SystemLogService logService) {
        this.showtimeRepository = showtimeRepository;
        this.logService = logService;
    }


    public Showtime createShowtime(Showtime showtime, Long adminId) {
        Showtime saved = showtimeRepository.save(showtime);
        logService.logAction(adminId, "Created showtime ID: " + saved.getShowtimeId());
        return saved;
    }


    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }


    public Showtime updateShowtime(Long id, Showtime updated, Long adminId) {
        return showtimeRepository.findById(id).map(showtime -> {
            showtime.setDate(updated.getDate());
            showtime.setTime(updated.getTime());
            showtime.setHallId(updated.getHallId());
            showtime.setMovie(updated.getMovie());
            Showtime saved = showtimeRepository.save(showtime);
            logService.logAction(adminId, "Updated showtime ID: " + saved.getShowtimeId());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Showtime not found"));
    }


    public void deleteShowtime(Long id, Long adminId) {
        showtimeRepository.deleteById(id);
        logService.logAction(adminId, "Deleted showtime ID: " + id);
    }

    public long getShowtimeCount() {
        return showtimeRepository.count();
    }
}
