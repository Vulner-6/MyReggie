package com.myreggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myreggie.common.BaseContext;
import com.myreggie.common.R;
import com.myreggie.entity.ShoppingCart;
import com.myreggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/shoppingCart")
public class ShoppingCartController
{
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart)
    {
        log.info("购物车数据：{}",shoppingCart);

        // 设置用户 id，指定当前是哪个用户的购物车数据
        Long currentId= BaseContext.getCurrentId();  // 获取当前线程的 id
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        // 查询当前菜品或套餐是否在购物车中
        Long dishId=shoppingCart.getDishId();


        // 若前端传来的参数有菜品 id，则说明此时在购物车中添加的是菜品不是套餐
        if(dishId!=null)
        {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else
        {
            // 否则此刻前端传来的就是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne=shoppingCartService.getOne(lambdaQueryWrapper);

        // 如果已经存在，则数量在原来基础上加一
        if(cartServiceOne!=null)
        {
            Integer number=cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }
        // 如果不存在，则默认添加到购物车，数量默认就是一
        else
        {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list()
    {
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list=shoppingCartService.list(lambdaQueryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean()
    {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");
    }

}
