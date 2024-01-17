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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final OrderRepository orderRepository;
    private final GuideRepository guideRepository;

    public void createScheduleForGuide(LocalDate date, String scheduleFilePath) {
        List<Order> allOrders = orderRepository.findAll();
        List<Order> ordersThisWeek = allOrders.stream().filter(o -> o.getTourDate().isAfter(date) && o.getTourDate()
                .isBefore(date.plusDays(7))).toList();

        List<Guide> allGuides = guideRepository.findAll();
        for (Guide guide : allGuides) {
            for (Order order : ordersThisWeek) {
                clearScheduleFile(scheduleFilePath);
                if (order.getGuide().equals(guide)) {

                }
            }
        }

    }

    public void clearScheduleFile(String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false));
            bw.write("");
            bw.close();
            System.out.println("The file has been cleared");
        } catch (IOException e) {
            System.out.println("An error occurred" + e.getMessage());
        }
    }




}
