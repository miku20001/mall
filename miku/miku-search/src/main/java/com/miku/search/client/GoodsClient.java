package com.miku.search.client;

import com.miku.item.api.GoodsApi;
import com.miku.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

}
