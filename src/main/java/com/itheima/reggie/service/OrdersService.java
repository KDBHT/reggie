package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.Orders;


public interface OrdersService extends IService<Orders> {
    /*
     * 用户下单
     * */
    public void sumbit(Orders orders);
}
