package com.myreggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myreggie.common.R;
import com.myreggie.entity.Category;
import com.myreggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/category")
public class CategoryController
{
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param request
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(HttpServletRequest request, @RequestBody Category category)
    {
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功！");
    }

    /**
     * 分类标签分页查询功能
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize)
    {
        // 分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        // 添加排序条件，根据 sort 进行排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        // 进行分页查询
        categoryService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids)
    {
        log.info("删除分类，id为：{}",ids);

        // CategoryService 继承自 MybatisPlus 中的 Iservice.class ，所以才有默认的 removeById() 方法。
        // 但是由于删除之前得做一个判断，因此需要自己定义一个方法，所以这里注释掉
        //categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("分类信息删除成功！");
    }

    /**
     * 编辑分类
     * 根据 id 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category)
    {
        log.info("修改分类:{}",category);
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 获取菜品分类
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category)
    {
        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper();
        // 添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        // 添加排序条件，根据更新时间排序
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 开始查询
        List<Category> list=categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }
}
