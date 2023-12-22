package com.myreggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myreggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail>
{
}
