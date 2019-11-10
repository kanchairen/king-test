package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.CategoryDao;
import com.lky.entity.Category;
import com.lky.entity.ProductGroup;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;

/**
 * 类目管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@Service
public class CategoryService extends BaseService<Category, Integer> {

    public static final int LEVEL_ONE = 1;
    public static final int LEVEL_TWO = 2;
    public static final int LEVEL_THREE = 3;

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private ProductGroupService productGroupService;

    @Override
    public BaseDao<Category, Integer> getBaseDao() {
        return this.categoryDao;
    }

    public long countByLevelAndName(int level, String name) {
        SimpleSpecificationBuilder<Category> builder = new SimpleSpecificationBuilder<>();
        builder.add("name", SpecificationOperator.Operator.eq, name);
        builder.add("level", SpecificationOperator.Operator.eq, level);
        return super.count(builder.generateSpecification());
    }

    public List<Category> listByLevel(Integer level) {
        return categoryDao.findByLevel(level);
    }

    public List<Category> listByParentId(Integer parentId) {
        return categoryDao.findByParentId(parentId);
    }

    public void create(Category category) {
        switch (category.getLevel()) {
            case LEVEL_THREE:
                AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, category.getLogoImg());
                break;
            default:
        }
        if (category.getParentId() != null) {
            Category parentCategory = super.findById(category.getParentId());
            AssertUtils.notNull(PARAMS_EXCEPTION, parentCategory);
        }
        super.save(category);
    }

    /**
     * 根据类目id获取该类目及其所有子类目
     *
     * @param id 类目id
     * @return 类目列表
     */
    public List<Category> findAllListById(Integer id) {
        List<Category> categoryList = new ArrayList<>();
        Category category = super.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, category);
        categoryList.add(category);
        if (category.getLevel() < LEVEL_THREE) {
            List<Category> categoryChildList = this.findListByParentId(category.getId());
            categoryList.addAll(categoryChildList);
            if (category.getLevel() == LEVEL_ONE) {
                if (!CollectionUtils.isEmpty(categoryChildList)) {
                    categoryChildList.forEach(categoryChild ->
                            categoryList.addAll(this.findListByParentId(categoryChild.getId())));
                }
            }
        }
        return categoryList;
    }

    /**
     * 根据类目id获取其所有子类目
     *
     * @param id 类目id
     * @return 类目列表
     */
    public List<Category> findListByParentId(Integer id) {
        SimpleSpecificationBuilder<Category> builder = new SimpleSpecificationBuilder<>();
        builder.add("parentId", SpecificationOperator.Operator.eq, id);
        return super.findAll(builder.generateSpecification());
    }

    public Long sumUsed(List<Category> categoryList) {
        long count = 0;
        for (Category category : categoryList) {
            SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
            builder.add("category", SpecificationOperator.Operator.eq, category.getId());
            count += productGroupService.count(builder.generateSpecification());
        }
        return count;
    }

    /**
     * 根据类目id获取其所父类目
     *
     * @param id 类目id
     * @return 类目列表
     */
    public List<Category> findListByChildId(Integer id) {
        List<Category> categoryList = new ArrayList<>();
        Category category = super.findById(id);
        if (category != null) {
            categoryList.add(category);
            while (category.getParentId() != null) {
                Category parentCategory = super.findById(category.getParentId());
                if (parentCategory != null) {
                    categoryList.add(parentCategory);
                    category = parentCategory;
                } else {
                    break;
                }
            }
        }
        return categoryList;
    }
}
