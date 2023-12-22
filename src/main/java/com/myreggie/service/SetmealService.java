package com.myreggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myreggie.dto.SetmealDto;
import com.myreggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal>
{
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐的同时，也删除对应的菜品关系
     * @param
     */
    public void removeWithDish(List<Long> ids);
}
