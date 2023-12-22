package com.myreggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myreggie.common.R;
import com.myreggie.dto.DishDto;
import com.myreggie.entity.Category;
import com.myreggie.entity.Dish;
import com.myreggie.entity.DishFlavor;
import com.myreggie.service.CategoryService;
import com.myreggie.service.DishFlavorService;
import com.myreggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/dish")
public class DishController
{
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存菜品信息
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto)
    {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name)
    {
        // 构造分页构造器对象
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        // 由于前端中需要 CategoryName 的值，所以只有 DishDto.java 中包装了，需要进行数据拷贝。
        Page<DishDto> dishDtoPage=new Page<>();

        // 条件构造器，添加过滤条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);

        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);

        // 对象拷贝,忽略分页构造器中的 records 属性，推测是数据列表，后面对 records 单独处理
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records=pageInfo.getRecords();
        List<DishDto> list=records.stream().map(
                (item)->
                {
                    DishDto dishDto=new DishDto();
                    BeanUtils.copyProperties(item,dishDto);
                    Long categoryId=item.getCategoryId();  // 分类 id
                    // 根据分类 id 查询分类对象
                    Category category=categoryService.getById(categoryId);
                    if(category!=null)
                    {
                        String categoryName=category.getName();
                        dishDto.setCategoryName(categoryName);
                    }


                    return dishDto;
                }
        ).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 获取菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id)
    {
        DishDto dishDto=dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto)
    {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }


    /**
     * 根据条件查询菜品数据，用于套餐中新增菜品时，获取菜品数据的查询
     * @param dish
     * @return
     */
    /** 重写该方法，使得前端可以同时满足移动端与PC端的需求
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish)
    {
        // 构造查询条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        // 再添加一个额外的条件，要求 status 为 1 （起售状态）时的数据
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        // 添加排序条件,若sort相同，则再根据时间排序
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list=dishService.list(lambdaQueryWrapper);


        return R.success(list);
    }

    */


    /**
     * 根据条件查询菜品数据，用于套餐中新增菜品时，获取菜品数据的查询
     * @param dish
     * @return
     */

     @GetMapping("/list")
     public R<List<DishDto>> list(Dish dish)
     {
         // 构造查询条件
         LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
         lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
         // 再添加一个额外的条件，要求 status 为 1 （起售状态）时的数据
         lambdaQueryWrapper.eq(Dish::getStatus,1);
         // 添加排序条件,若sort相同，则再根据时间排序
         lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

         List<Dish> list=dishService.list(lambdaQueryWrapper);

         List<DishDto> dishDtoList=list.stream().map(
                 (item)->
                 {
                     DishDto dishDto=new DishDto();
                     BeanUtils.copyProperties(item,dishDto);
                     Long categoryId=item.getCategoryId();  //分类 id
                     Category category=categoryService.getById(categoryId);

                     if(category!=null)
                     {
                         String categoryName=category.getName();
                         dishDto.setCategoryName(categoryName);
                     }

                     // 当前菜品 id
                     Long dishId=item.getId();
                     // 构造查询条件，根据菜品 id 去菜品口味关系表中查询该菜品的口味列表
                     LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
                     queryWrapper.eq(DishFlavor::getDishId,dishId);
                     List<DishFlavor> dishFlavorList=dishFlavorService.list(queryWrapper);

                     // 设置 flavors 至 DTO 对象，方便一起传回前端
                     dishDto.setFlavors(dishFlavorList);

                     return dishDto;

                 }
         ).collect(Collectors.toList());

         return R.success(dishDtoList);
     }




}
