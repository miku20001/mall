package com.miku.elasticsearch.test;

import com.miku.common.pojo.PageResult;
import com.miku.item.bo.SpuBo;
import com.miku.search.client.GoodsClient;
import com.miku.search.pojo.Goods;
import com.miku.search.repository.GoodsRepository;
import com.miku.search.service.Old;
import com.miku.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void test(){
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);

        Integer page = 1;
        Integer rows = 100;

        do{
            //分页查询spu
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuByPage(null, null, page, rows);
            List<SpuBo> items = pageResult.getItems();

            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            this.goodsRepository.saveAll(goodsList);

            rows = items.size();
            page++;
        }while (rows==100);



    }

    @Test
    public void test2(){
        System.out.println("hello");
    }
}
