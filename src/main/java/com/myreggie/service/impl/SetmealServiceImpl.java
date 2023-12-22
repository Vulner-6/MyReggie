package com.myreggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myreggie.common.CustomException;
import com.myreggie.dto.SetmealDto;
import com.myreggie.entity.Setmeal;
import com.myreggie.entity.SetmealDish;
import com.myreggie.mapper.SetmealMapper;
import com.myreggie.service.SetmealDishService;
import com.myreggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService
{
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 实现保存套餐时，还保存套餐对应的菜品关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto)
    {
        // 保存套餐基本信息，操作 setmeal ，执行 insert 操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();
        setmealDishes.stream().map(
                (item)->
                    {
                        item.setSetmealId(setmealDto.getId());
                        return item;
                    }
                ).collect(Collectors.toList());

        // 保存套餐菜品的关联信息，操作 setmeal_dish ,执行 insert 操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除对应的菜品关系
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids)
    {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper();
        // 查询套餐状态，判断是否可以删除，只有状态为禁用的情况下才能删除
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);

        // 如果不能删除，抛出业务异常
        int count=this.count(lambdaQueryWrapper);
        if(count>0)
        {
            throw new CustomException("套餐正在售卖中，不能删除！");
        }

        // 如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        // 再删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);


    }

}
