package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加套餐成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page pageinfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageinfo,lambdaQueryWrapper);
        return R.success(pageinfo);
    }
    @DeleteMapping
    public R<String> delete(Long ids){
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("更新分类信息成功");
    }
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件,如果获取到对象的type不为空时才进行查询
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        categoryLambdaQueryWrapper.orderByAsc(Category::getType).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(categoryList);
    }
}
