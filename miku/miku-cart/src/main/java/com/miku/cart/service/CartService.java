package com.miku.cart.service;

import com.miku.cart.client.GoodsClient;
import com.miku.cart.interrceptor.LoginInterceptor;
import com.miku.cart.pojo.Cart;
import com.miku.common.pojo.UserInfo;
import com.miku.common.utils.JsonUtils;
import com.miku.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "user:cart:";

    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();


        //查询购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();

        //判断当前商品是否在购物车中
        if(hashOperations.hasKey(key)) {

            //在，更新数量
            String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();

            cart = JsonUtils.parse(cartJson, Cart.class);

            cart.setNum(cart.getNum() + num);

            hashOperations.put(key, JsonUtils.serialize(cart));


        }else{
            //不在，新增购物车
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());

            hashOperations.put(key,JsonUtils.serialize(cart));
        }






    }

    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return null;
        }


        //获取用户的购物车
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        List<Object> cartsJson = hashOperations.values();

        if(CollectionUtils.isEmpty(cartsJson)){
            return null;
        }

        return cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(),Cart.class)).collect(Collectors.toList());

    }

    public void updateCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return;
        }

        Integer num = cart.getNum();

        //获取用户的购物车
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        cart = JsonUtils.parse(cartJson, Cart.class);

        cart.setNum(num);

        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }
}
