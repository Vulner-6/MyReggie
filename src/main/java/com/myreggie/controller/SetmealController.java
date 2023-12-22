package com.myreggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myreggie.common.R;
import com.myreggie.dto.SetmealDto;
import com.myreggie.entity.Category;
import com.myreggie.entity.Setmeal;
import com.myreggie.service.CategoryService;
import com.myreggie.service.SetmealDishService;
import com.myreggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/setmeal")
public class SetmealController
{
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto)
    {
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name)
    {
        // 分页构造器对象
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        // 用于返回包含 DTO 类中独有字段的分页对象
        Page<SetmealDto> dtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper();
        // 添加查询条件，根据 name 进行 like 模糊查询
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        // 添加排序条件，根据更新时间降序
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,lambdaQueryWrapper);

        // 将 pageInfo 对象信息复制给 dtoPage ，忽略 records 属性的拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        // 自己重组需要的 records 信息
        List<Setmeal> records=pageInfo.getRecords();

        List<SetmealDto> list=records.stream().map(
                (item)->
                {
                    SetmealDto setmealDto=new SetmealDto();
                    BeanUtils.copyProperties(item,setmealDto);
                    // 分类 id
                    Long categoryId=item.getCategoryId();
                    // 根据分类 id 查询分类对象
                    Category category=categoryService.getById(categoryId);
                    if (category!=null)
                    {
                        // 分类名称
                        String categoryName=category.getName();
                        setmealDto.setCategoryName(categoryName);
                    }
                    return setmealDto;

                }
        ).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);


    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids)
    {
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功！");
    }

    /**
     * 在移动端选择套餐时，展示套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list=setmealService.list(queryWrapper);
        return R.success(list);

    }
}
