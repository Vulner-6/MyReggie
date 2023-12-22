package com.myreggie.dto;

import com.myreggie.entity.Dish;
import com.myreggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish  // 继承自Dish，所以部分Dish的字段在这里也能用来接收前端传来的值
{

    private List<DishFlavor> flavors = new ArrayList<>();

    // 暂时还未用到，后面项目会用
    private String categoryName;
    // 暂时还未用到，后面项目会用
    private Integer copies;
}
