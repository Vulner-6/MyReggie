package com.myreggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myreggie.entity.Orders;

public interface OrdersService extends IService<Orders>
{
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
