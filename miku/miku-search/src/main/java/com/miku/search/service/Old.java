package com.miku.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miku.item.pojo.*;
import com.miku.search.client.BrandClient;
import com.miku.search.client.CategoryClient;
import com.miku.search.client.GoodsClient;
import com.miku.search.client.SpecificationClient;
import com.miku.search.pojo.Goods;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Old {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();


        //根据分类id查询名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //根据spuId查询所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());

        //初始化一个价格集合，收集所有sku的价格参数
        List<Long> prices = new ArrayList<>();

        //收集必要的sku字段
        List<Map<String,Object>> skuMapList = new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("Image",StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);

            skuMapList.add(map);
        });

//        List<Long> prices = skus.stream().map(sku -> sku.getPrice()).collect(Collectors.toList());

        List<SpecParam> params = this.specificationClient.queryParamByGid(null, spu.getCid3(), null, true);

        Map<String,Object> paramsMap = new HashMap<>();
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());

        String genericSpec = spuDetail.getGenericSpec();

        //把通用规格参数值，进行反序列化
        Map<String,Object> genericSpecMap = MAPPER.readValue(genericSpec, new TypeReference<Map<String,Object>>(){});
        
        //把特殊规格参数值，进行反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {});


        params.forEach(param -> {
            //判断规格参数的类型是否是通用的规格参数
            if(param.getGeneric()){
                //如果是通用，则从通用规格参数取值
                String value = genericSpecMap.get(param.getId().toString()).toString();

                //判断是否是数值类型，数值类型应该返回一个区间
                if(param.getNumeric()){
                    value = chooseSegment(value, param);
                }
                paramsMap.put(param.getName(),value);
            }
            else {

                //如果是特殊，则从特殊规格参数获取
                List<Object> value = specialSpecMap.get(param.getId().toString());

                paramsMap.put(param.getName(),value);
            }


        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());

        //拼接all字段
        goods.setAll(spu.getTitle() +" "+ StringUtils.join(names," ") +" "+ brand.getName());

        //获取spu下所有价格
        goods.setPrice(prices);

        //获取spu下所有sku，并转化为json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));

        //获取所有查询的规格参数 {name:value}
        goods.setSpecs(paramsMap);
        return goods;
    }


    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
