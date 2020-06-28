package com.miku.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId){

        //初始化运行上下文
        Context context = new Context();

        Map<String, Object> map = this.goodsService.loadData(spuId);
        context.setVariables(map);

        PrintWriter printWriter = null;
        try {
            File file = new File("F:\\nginx-1.16.1\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);

            this.templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(printWriter!=null){
                printWriter.close();
            }
        }
    }

    public void deleteHtml(Long id) {
        File file = new File("F:\\nginx-1.16.1\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
