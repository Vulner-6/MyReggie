package com.myreggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myreggie.common.CustomException;
import com.myreggie.entity.Category;
import com.myreggie.entity.Dish;
import com.myreggie.entity.Setmeal;
import com.myreggie.mapper.CategoryMapper;
import com.myreggie.service.CategoryService;
import com.myreggie.service.DishService;
import com.myreggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService
{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 自定义的删除分类前需要先判断是否关联菜品的方法，根据传入的id进行删除
     * @param id
     */
    @Override
    public void remove(Long id)
    {
        // 构造分类是否关联菜品的查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dish_count=dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联菜品，若关联则抛出异常
        if(dish_count>0)
        {
            // 已经关联了菜品，需要抛出业务异常
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }

        // 构造分类是否关联套餐的查询条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmeal_count=setmealService.count(setmealLambdaQueryWrapper);

        // 查询当前分类是否关联套餐，若关联则抛出异常
        if(setmeal_count>0)
        {
            // 已经关联了套餐，需要抛出业务异常
            throw new CustomException("当前分类关联了套餐，不能删除！");
        }

        // 若都不关联，则正常删除分类
        super.removeById(id);
    }
}
