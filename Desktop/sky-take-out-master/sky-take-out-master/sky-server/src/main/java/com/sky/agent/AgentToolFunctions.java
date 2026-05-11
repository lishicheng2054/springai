package com.sky.agent;

import com.alibaba.fastjson.JSON;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AgentToolFunctions {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查询菜品列表
     * @param categoryName 分类名称，可为空
     */
    public String queryDish(String categoryName) {
        try {
            Dish query = new Dish();
            query.setStatus(1); // 只查在售菜品
            List<Dish> dishes = dishMapper.list(query);

            if (categoryName != null && !categoryName.trim().isEmpty()) {
                String keyword = categoryName.trim();
                dishes = dishes.stream()
                        .filter(d -> d.getName() != null && d.getName().contains(keyword))
                        .collect(Collectors.toList());
            }

            if (dishes.isEmpty()) {
                return "未找到相关菜品";
            }

            List<Map<String, Object>> result = dishes.stream().limit(10).map(d -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", d.getId());
                item.put("name", d.getName());
                item.put("price", d.getPrice());
                item.put("description", d.getDescription());
                return item;
            }).collect(Collectors.toList());

            return JSON.toJSONString(result);
        } catch (Exception e) {
            log.error("queryDish 失败", e);
            return "查询菜品失败：" + e.getMessage();
        }
    }

    /**
     * 将菜品加入购物车
     * @param dishName 菜品名称
     */
    public String addToCart(String dishName) {
        try {
            Dish query = new Dish();
            query.setStatus(1);
            List<Dish> dishes = dishMapper.list(query);

            Dish target = dishes.stream()
                    .filter(d -> d.getName() != null && d.getName().contains(dishName.trim()))
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                return "未找到菜品：" + dishName + "，请确认菜品名称是否正确";
            }

            ShoppingCartDTO cartDTO = new ShoppingCartDTO();
            cartDTO.setDishId(target.getId());
            shoppingCartService.addShoppingCart(cartDTO);

            return "已将「" + target.getName() + "」加入购物车，价格：¥" + target.getPrice();
        } catch (Exception e) {
            log.error("addToCart 失败", e);
            return "加入购物车失败：" + e.getMessage();
        }
    }

    /**
     * 查询当前用户最近订单
     */
    public String queryOrder() {
        try {
            Long userId = BaseContext.getCurrentId();
            if (userId == null) {
                return "请先登录后再查询订单";
            }

            com.sky.dto.OrdersPageQueryDTO queryDTO = new com.sky.dto.OrdersPageQueryDTO();
            queryDTO.setUserId(userId);
            queryDTO.setPage(1);
            queryDTO.setPageSize(5);

            com.github.pagehelper.Page<Orders> page = orderMapper.pageQuery(queryDTO);
            List<Orders> orders = page.getResult();

            if (orders == null || orders.isEmpty()) {
                return "暂无订单记录";
            }

            List<Map<String, Object>> result = orders.stream().map(o -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", o.getId());
                item.put("number", o.getNumber());
                item.put("amount", o.getAmount());
                item.put("status", getStatusText(o.getStatus()));
                item.put("orderTime", o.getOrderTime() != null ? o.getOrderTime().toString() : "");
                item.put("address", o.getAddress());
                return item;
            }).collect(Collectors.toList());

            return JSON.toJSONString(result);
        } catch (Exception e) {
            log.error("queryOrder 失败", e);
            return "查询订单失败：" + e.getMessage();
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "待付款";
            case 2: return "待接单";
            case 3: return "已接单";
            case 4: return "派送中";
            case 5: return "已完成";
            case 6: return "已取消";
            default: return "未知";
        }
    }
}
