package com.App.GetYourGuide.service;

import com.App.GetYourGuide.domain.*;
import com.App.GetYourGuide.mapper.OrderMapper;
import com.App.GetYourGuide.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private GuideService guideService;

    private OrderDto orderOneDto;
    private Order orderOne;
    private Guide guideOne;
    private final List<Order> guideOneTours = new ArrayList<>();
    private Customer customerOne;

    @BeforeEach
    public void setUp() {
        customerOne = new Customer();
        TourOrder firstOrderDecorator = new InsuranceDecorator(new EquipmentDecorator(new BasicOrderDecorator()));
        orderOne = new Order(1L, firstOrderDecorator, guideOne, LocalDate.of(2023, 9, 30), true, true, true,
                customerOne);
        orderOneDto = new OrderDto(1L, firstOrderDecorator, new GuideDto(), LocalDate.of(2023, 9, 30), true,
                true, true, new CustomerDto());
        Order orderTwo = new Order(2L, firstOrderDecorator, guideOne, LocalDate.of(2023, 10, 1), true, true, true, new Customer());
        guideOneTours.add(orderOne);
        guideOneTours.add(orderTwo);

        guideOne = new Guide(1L, "Name Lastname", guideOneTours);
    }

    @Test
    void shouldGetOrderById() {
        //Given
        when(orderMapper.mapToOrderDetailsDto(orderOne)).thenReturn(orderOneDto);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderOne));
        //When
        Optional<OrderDto> order = orderService.getOrder(1L);
        //Then
        assertTrue(order.isPresent());
        assertEquals(orderOneDto, order.get());
        assertEquals(650, order.get().getTour().getCost());
        assertEquals("Mountain trip with a guide, with equipment rental, with mountaineering insurance", order.get()
                .getTour().getDescription());
    }

    @Test
    void getAllOrders() {
    }

    @Test
    void shouldCancelOrder() {
        //Given & When
        orderService.cancelOrder(orderOne.getOrderId());
        //Then
        verify(orderRepository, times(1)).deleteById(orderOne.getOrderId());
    }

    @Test
    void shouldCreateOrder() {
        //Given
        when(guideService.getAvailableGuides(LocalDate.of(2023, 10, 2))).thenReturn((List<Guide>) guideOne);
        //When
        orderService.createOrder(customerOne, LocalDate.of(2023, 10, 2));
        //Then
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldUpdateOrder() {
        //Given
        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(orderOne));
        //When
        OrderDto updatedOrder = orderService.updateOrderDetails(1L, LocalDate.of(2023, 10, 30));
        //Then
        assertEquals(LocalDate.of(2023, 10, 30), updatedOrder.getTourDate());
    }

    @Test
    void shouldReturnRefundPayment() {
        //Given
        when(orderRepository.getReferenceById(1L)).thenReturn(orderOne);
        //When
        double refund = orderService.refundPayment(1L);
        //Then
        assertEquals(650, refund);
    }
}