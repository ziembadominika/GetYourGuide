package com.App.GetYourGuide.service;

import com.App.GetYourGuide.domain.Guide;
import com.App.GetYourGuide.domain.Order;
import com.App.GetYourGuide.repository.GuideRepository;
import com.App.GetYourGuide.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final GuideRepository guideRepository;
    private final EmailService emailService;
    private final OrderService orderService;

    public void createScheduleForGuide(LocalDate date, String scheduleFilePath) {
        List<Order> ordersThisWeek = orderService.filterOrdersForUpcomingWeek(date);
        clearScheduleFile(scheduleFilePath);

        for (Guide guide : guideRepository.findAll()) {
            StringBuilder scheduleBuilder = new StringBuilder();
            for (Order order : ordersThisWeek) {
                if (order.getGuide().equals(guide)) {
                    scheduleBuilder.append(order.getTourDate()).append(" ").append(order.getCustomer()
                            .getName()).append("\n");
                }
                if (scheduleBuilder.length() > 0) {
                    addTourToFile(scheduleFilePath, scheduleBuilder.toString());
                    emailService.sendEmailWithWeeklySchedule(guide.getEmail(), "path/to/weeklySchedule.txt");
                }
            }
        }
    }

    public void clearScheduleFile(String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false));
            bw.write("");
            System.out.println("The file has been cleared");
        } catch (IOException e) {
            System.out.println("An error occurred" + e.getMessage());
        }
    }

    public void addTourToFile(String filePath, String line) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred" + e.getMessage());
        }
    }
}
