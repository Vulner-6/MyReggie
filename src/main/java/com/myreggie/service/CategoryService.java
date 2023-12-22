package com.myreggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myreggie.entity.Category;

public interface CategoryService  extends IService<Category>
{
    // 自己扩展删除菜品时做判断的方法
    public void remove(Long id);
}
