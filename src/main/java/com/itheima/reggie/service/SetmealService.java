package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /*
    * 新增套餐，同时保存菜品关联信息
    * */
    public void saveWithDish(SetmealDto setmealDto);
    /*
    * 删除套餐，同时删除菜品关联信息
    * */
    public void removeWithDish(List<Long> ids);
}
