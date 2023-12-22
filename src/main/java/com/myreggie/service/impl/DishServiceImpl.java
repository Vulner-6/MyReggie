package com.myreggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myreggie.dto.DishDto;
import com.myreggie.entity.Dish;
import com.myreggie.entity.DishFlavor;
import com.myreggie.mapper.DishMapper;
import com.myreggie.service.DishFlavorService;
import com.myreggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService
{
    @Autowired
    private DishFlavorService dishFlavorService;

    // 实现接口中自己自定义的扩展方法，用于在保存菜品数据的同时，也保存口味数据至对应的口味表中
    @Transactional   //由于涉及到多张表的操作，所以需要加上事务的注解
    public void saveWithFlavor(DishDto dishDto)
    {
        // 保存菜品基本信息至菜品表 dish，因为 DishDto 继承自 Dish，所以可以直接保存双方共有的字段
        this.save(dishDto);

        // 提取菜品 id，用于保存口味时能够一一对应
        Long dishId=dishDto.getId();

        // 找到菜品口味 flavors 中的 dish_id 字段，并将其赋值
        List<DishFlavor> dishFlavors=dishDto.getFlavors();
        // 数据流的遍历写法，也可以使用其他的写法
        List<DishFlavor> newDishFlavors=dishFlavors.stream().map(
                (item)->
                {
                    item.setDishId(dishId);
                    return item;
                }
        ).collect(Collectors.toList());

        // 保存菜品口味数据至口味表 dish_flavor，由于 flavor 是集合，所以使用 saveBatch()
        dishFlavorService.saveBatch(newDishFlavors);
    }

    /**
     * 查询菜品信息以及对应的口味信息
     * @return
     */
    public DishDto getByIdWithFlavor(Long id)
    {
        // 查询菜品基本信息，从 dish 表查询
        Dish dish=this.getById(id);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询当前菜品对应的口味信息,从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品表信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto)
    {
        // 更新菜品表基本信息
        this.updateById(dishDto);

        // 清理当前菜品口味数据 --- dish_flavor 表 delete 操作
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加当前提交过来的口味数据 --- dish_flavor 表的 insert 操作
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map(
                (item)->
                {
                    item.setDishId(dishDto.getId());
                    return item;
                }
        ).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }
}
