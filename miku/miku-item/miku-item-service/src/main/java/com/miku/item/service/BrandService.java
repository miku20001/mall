package com.miku.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miku.common.pojo.PageResult;
import com.miku.item.mapper.BrandMapper;
import com.miku.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, boolean desc){
        //根据name做模糊查询，或者根据首字母查询
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();


        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }


        //添加分页条件
        PageHelper.startPage(page,rows);
        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc ? "desc":"asc" ));
        }


        List<Brand> brands = this.brandMapper.selectByExample(example);

        //包装成PageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        //包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional  //事务标签
    public void saveBrand(Brand brand, List<Long> cids){

        //先新增品牌
        boolean flag = this.brandMapper.insertSelective(brand)==1;

        if(flag){
            cids.forEach(cid ->{
                this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
            });

        }
    }


    /**
     * 根据分类id查询品牌列表
     * @param cid
     * @return
     */
    public List<Brand> queryBrandsByCid(Long cid){

        return this.brandMapper.selectBrandsByCid(cid);  //多表操作，自定义sql
    }

    public Brand queryBrandById(Long id){
        return this.brandMapper.selectByPrimaryKey(id);
    }

}
