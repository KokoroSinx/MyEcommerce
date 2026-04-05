package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl  implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;

    /**
     * Add dish or setmeal to cart
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // set user id login
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // if already exist, merge, only add numer + 1
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        // in this duplicated situation, we need to get number + 1
        // 我tmd查了entity才发现amount是金额，bigdecimal，而number才是数量，写这个教程的人只能说英语有点差
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            shoppingCart = shoppingCartList.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            // 只改数量不insert不改别的，为了这个最简操作好像是专门写了个sql方法
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {
            // 如果不存在应该就是要insert，那么数量此时应该为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            // Because both dish and setmeal is stored in 1 table, we need to judge
            // by nullable
            Long dishId = shoppingCart.getDishId();
            if (dishId != null) {
                // Dish
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                log.info("shoppingCart before insert: {}", shoppingCart);
                log.info("dish price: {}", dish.getPrice());
            } else {
                // Setmeal
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }

            // insert operation
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * View the cart
     */
    public List<ShoppingCart> showShoppingCart() {
        return shoppingCartMapper.list(ShoppingCart
                .builder()
                .userId(BaseContext.getCurrentId())
                .build());
    }

    /**
     * Clear the cart
     */
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
