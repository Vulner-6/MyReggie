package com.myreggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myreggie.dto.DishDto;
import com.myreggie.entity.Dish;

public interface DishService extends IService<Dish>
{
    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish，dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据 id 获取菜品信息，同时获取口味
    public DishDto getByIdWithFlavor(Long id);

    // 跟新菜品信息,同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);
}
