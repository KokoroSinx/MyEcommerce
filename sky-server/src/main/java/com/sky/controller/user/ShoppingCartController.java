package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "客户端-购物车接口")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * Add to shopping cart
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("Add to cart: {}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * View the cart
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("View the Cart")
    public Result<List<ShoppingCart>> list() {
        log.info("View the Cart: {}", BaseContext.getCurrentId());
        return Result.success(shoppingCartService.showShoppingCart());
    }

    /**
     * Clear the cart
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("Clear the cart")
    public Result clean() {
        log.info("Clean the cart: {}", BaseContext.getCurrentId());
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}
