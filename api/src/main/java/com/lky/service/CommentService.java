package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.CommentDao;
import com.lky.dto.CommentCountDto;
import com.lky.dto.CommentDto;
import com.lky.dto.CreateComment;
import com.lky.entity.*;
import com.lky.enums.dict.CommentDict;
import com.lky.mapper.CommentMapper;
import com.lky.mapper.ImageMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.dict.CommentDict.*;

/**
 * 商品组评论
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/27
 */
@Service
public class CommentService extends BaseService<Comment, Integer> {

    @Inject
    private CommentDao commentDao;

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private OrdersService ordersService;

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private ProductService productService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private ShopService shopService;

    @Override
    public BaseDao<Comment, Integer> getBaseDao() {
        return this.commentDao;
    }

    public void create(User user, List<CreateComment> createCommentList) {
        createCommentList.forEach(createComment -> {
            Integer score = createComment.getScore();
            Integer ordersItemId = createComment.getOrdersItemId();
            String content = createComment.getContent();
            List<Image> commentImgList = createComment.getCommentImgList();

            AssertUtils.notNull(PARAMS_IS_NULL, score, ordersItemId);
            OrdersItem ordersItem = ordersItemService.findById(ordersItemId);
            AssertUtils.isTrue(PARAMS_EXCEPTION, ordersItem.getUserId() == user.getId());

            Orders orders = ordersService.findById(ordersItem.getOrdersId());
            AssertUtils.isTrue(NOT_AUTHORIZED, orders.getState().equals("over"));
            AssertUtils.isTrue(PARAMS_EXCEPTION, score >= 1 && score <= 5);
            Product product = ordersItem.getProduct();
            ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
            productGroup.setCommentNumber(productGroup.getCommentNumber() + 1);
            product.setCommentNumber(product.getCommentNumber() + 1);

            AssertUtils.notNull(PARAMS_EXCEPTION, product, productGroup);

            Comment comment = new Comment();
            if (StringUtils.isNotEmpty(createComment.getContent())) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, content.length() <= 1024);
                comment.setContent(content);
            }
            comment.setBuyTime(orders.getCreateTime());
            comment.setCommentImgIds(imageMapper.imgListToStr(commentImgList));
            comment.setOrdersId(orders.getId());
            comment.setScore(score);
            comment.setProductId(product.getId());
            comment.setProductGroupId(productGroup.getId());
            comment.setUser(user);
            comment.setCreateTime(new Date());
            comment.setOrdersItemId(ordersItemId);
            //订单评论状态
            if (orders.getComment() != null || orders.getComment() != Boolean.TRUE) {
                orders.setComment(Boolean.TRUE);
                ordersService.update(orders);
            }
            ordersItem.setComment(Boolean.TRUE);
            ordersItemService.update(ordersItem);
            super.save(comment);
        });
    }

    public void createAppend(User user, List<CreateComment> createCommentList) {
        createCommentList.forEach(createComment -> {
            if (createComment.getContent() != null || createComment.getCommentImgList() != null) {
                Comment comment = super.findById(createComment.getId());
                AssertUtils.isTrue(PARAMS_EXCEPTION, comment != null && user == comment.getUser());
                String content = createComment.getContent();
                List<Image> commentImgList = createComment.getCommentImgList();
                assert comment != null;
                comment.setAppendCommentImgIds(imageMapper.imgListToStr(commentImgList));

                OrdersItem ordersItem = ordersItemService.findById(comment.getOrdersItemId());
                AssertUtils.isTrue(PARAMS_EXCEPTION, ordersItem.getUserId() == user.getId());
                Orders orders = ordersService.findById(ordersItem.getOrdersId());
                if (orders.getAppendComment() == null || orders.getAppendComment() != null) {
                    orders.setAppendComment(Boolean.TRUE);
                    ordersService.update(orders);
                }
                ordersItem.setAppendComment(Boolean.TRUE);
                ordersItemService.update(ordersItem);

                if (StringUtils.isNotEmpty(content)) {
                    AssertUtils.isTrue(PARAMS_EXCEPTION, content.length() <= 1024);
                    comment.setAppendContent(content);
                }
                comment.setAppendTime(new Date());
                super.update(comment);
            }
        });
    }

    public Page<CommentDto> listByProductAndType(Integer productGroupId, String type, Pageable pageable) {

        Specification<Comment> spec = (root, query, cb) -> {
            Predicate p1 = cb.equal(root.get("productGroupId"), productGroupId);
            if (StringUtils.isNotEmpty(type)) {
                CommentDict anEnum = CommentDict.getEnum(type);
                AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_IMAGE, TYPE_HIGH, TYPE_MIDDLE, TYPE_LOW, TYPE_APPEND);
                switch (anEnum) {
                    case TYPE_IMAGE:
                        Predicate p2 = cb.isNotNull(root.get("commentImgIds"));
                        Predicate p3 = cb.isNotNull(root.get("appendCommentImgIds"));
                        return cb.and(p1, cb.or(p2, p3));
                    case TYPE_HIGH:
                        return cb.and(p1, cb.gt(root.get("score"), 3));
                    case TYPE_MIDDLE:
                        return cb.and(p1, cb.equal(root.get("score"), 3));
                    case TYPE_LOW:
                        return cb.and(p1, cb.lt(root.get("score"), 3));
                    case TYPE_APPEND:
                        return cb.and(p1, cb.isNotNull(root.get("appendTime")));
                }
            }
            return cb.and(p1);
        };
        Page<Comment> commentListPage = super.findAll(spec, pageable);
        List<CommentDto> commentDtoList = commentMapper.toDtoList(commentListPage.getContent());
        return new PageImpl<>(commentDtoList, pageable, commentListPage.getTotalElements());
    }

    public CommentCountDto countByProductGroup(Integer productGroupId) {
        CommentCountDto commentCountDto = new CommentCountDto();
        SimpleSpecificationBuilder<Comment> builder = new SimpleSpecificationBuilder<>();
        builder.add("productGroupId", SpecificationOperator.Operator.eq, productGroupId);
        List<Comment> commentList = super.findAll(builder.generateSpecification());
        int haveImage = 0;
        int high = 0;
        int middle = 0;
        int low = 0;
        int append = 0;
        if (!CollectionUtils.isEmpty(commentList)) {
            for (Comment comment : commentList) {
                if (comment.getCommentImgIds() != null || comment.getAppendCommentImgIds() != null) {
                    ++haveImage;
                }
                if (comment.getAppendTime() != null) {
                    ++append;
                }
                if (comment.getScore() > 3) {
                    ++high;
                    continue;
                }
                if (comment.getScore() == 3) {
                    ++middle;
                    continue;
                }
                if (comment.getScore() < 3) {
                    ++low;
                }
            }
        }
        commentCountDto.setAllNumber(commentList.size());
        commentCountDto.setHaveImage(haveImage);
        commentCountDto.setHigh(high);
        commentCountDto.setMiddle(middle);
        commentCountDto.setLow(low);
        commentCountDto.setAppend(append);
        return commentCountDto;
    }

    public void reply(User user, CreateComment createComment) {
        Comment comment = super.findById(createComment.getId());
        //校验参数和权限
        AssertUtils.notNull(PARAMS_EXCEPTION, comment);
        ProductGroup productGroup = productGroupService.findById(comment.getProductGroupId());
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);
        AssertUtils.isTrue(NOT_AUTHORIZED, comment.getProductGroupId() == productGroup.getId() &&
                productGroup.getShop() == shopService.findByUser(user));
        OrdersItem ordersItem = ordersItemService.findById(comment.getOrdersItemId());
        Orders orders = ordersService.findById(ordersItem.getOrdersId());
        if (StringUtils.isNotEmpty(createComment.getReply())) {
            comment.setReply(createComment.getReply());
            comment.setReplyTime(new Date());
            ordersItem.setReply(Boolean.TRUE);
            if (orders.getReply() == null || orders.getReply() != Boolean.TRUE) {
                orders.setReply(Boolean.TRUE);
                ordersService.update(orders);
            }
        }
        if (StringUtils.isNotEmpty(createComment.getAppendReply())) {
            comment.setAppendReply(createComment.getAppendReply());
            comment.setAppendReplyTime(new Date());
            ordersItem.setAppendReply(Boolean.TRUE);
            if (orders.getAppendReply() == null || orders.getAppendComment() != Boolean.TRUE) {
                orders.setAppendReply(Boolean.TRUE);
                ordersService.update(orders);
            }
        }
        ordersItemService.update(ordersItem);
        super.update(comment);
    }

    public CommentDto findByOrdersItemId(Integer ordersItemId) {
        AssertUtils.notNull(PARAMS_EXCEPTION, ordersItemId);
        Comment comment = commentDao.findByOrdersItemId(ordersItemId);
        if (comment != null) {
            return commentMapper.toDto(comment);
        }
        return null;
    }
}
