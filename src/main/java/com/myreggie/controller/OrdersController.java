package com.myreggie.controller;

import com.myreggie.common.R;
import com.myreggie.entity.Orders;
import com.myreggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController
{
    @Autowired
    OrdersService ordersService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders)
    {
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功！");
    }
}
