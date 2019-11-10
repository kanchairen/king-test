package com.lky.mapper;

import com.lky.dto.CommentDto;
import com.lky.entity.Comment;
import com.lky.entity.Product;
import com.lky.service.ProductService;
import com.lky.utils.BeanUtils;
import org.mapstruct.Mapper;

import javax.inject.Inject;
import java.util.List;

/**
 * 商品评论Dto转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/31
 */
@Mapper(componentModel = "jsr330")
public abstract class CommentMapper {

    @Inject
    private ProductService productService;

    @Inject
    private ImageMapper imageMapper;

    public abstract List<CommentDto> toDtoList(List<Comment> commentList);

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        BeanUtils.copyPropertiesIgnoreNull(comment, commentDto);
        commentDto.setAppendCommentImgList(imageMapper.imgIdsToList(comment.getAppendCommentImgIds()));
        commentDto.setCommentImgList(imageMapper.imgIdsToList(comment.getCommentImgIds()));
        commentDto.setUserHead(comment.getUser().getAvatarImage());
        commentDto.setNickname(comment.getUser().getNickname());
        commentDto.setBuyTime(comment.getBuyTime());
        Product product = productService.findById(comment.getProductId());
        commentDto.setSpec(product.getSpec());
        commentDto.setProductName(product.getName());
        commentDto.setProductImg(product.getPreviewImg());
        return commentDto;
    }


}
