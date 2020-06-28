package com.miku.goods.Listener;

import com.miku.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MIKU.ITEM.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MIKU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void save(Long id){
        if(id == null){
            return;
        }
        this.goodsHtmlService.createHtml(id);

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MIKU.ITEM.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "MIKU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id){
        if(id == null){
            return;
        }
        this.goodsHtmlService.deleteHtml(id);

    }

}
