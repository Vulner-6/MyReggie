package com.myreggie.dto;

import com.myreggie.entity.Setmeal;
import com.myreggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal
{

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
