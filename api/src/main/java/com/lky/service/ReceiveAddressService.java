package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.JsonUtils;
import com.lky.dao.ReceiveAddressDao;
import com.lky.dto.AddressDto;
import com.lky.entity.ReceiveAddress;
import com.lky.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.enums.code.OrderResCode.NEED_DEFAULT_ADDRESS;

/**
 * 收货地址
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/25
 */
@Service
public class ReceiveAddressService extends BaseService<ReceiveAddress, Integer> {

    @Inject
    private ReceiveAddressDao receiveAddressDao;

    @Inject
    private AreaService areaService;

    @Override
    public BaseDao<ReceiveAddress, Integer> getBaseDao() {
        return this.receiveAddressDao;
    }

    public List<ReceiveAddress> findByUserId(Integer userId) {
        return receiveAddressDao.findByUserIdOrderByFirstDesc(userId);
    }

    public ReceiveAddress findByFirstAndUserId(Integer userId) {
        return receiveAddressDao.findByFirstAndUserId(Boolean.TRUE, userId);
    }

    public void create(User user, ReceiveAddress address) {
        areaService.verifyAddress(JsonUtils.jsonToObject(address.getAddressDetail(), AddressDto.class), Boolean.TRUE);
        address.setUserId(user.getId());
        if (address.getFirst()) {
            ReceiveAddress firstAddress = this.findByFirstAndUserId(user.getId());
            if (firstAddress != null) {
                firstAddress.setFirst(Boolean.FALSE);
                firstAddress.setUpdateTime(new Date());
                super.update(firstAddress);
            }
        }
        List<ReceiveAddress> receiveAddressList = findByUserId(user.getId());
        if (CollectionUtils.isEmpty(receiveAddressList)) {
            address.setFirst(Boolean.TRUE);
        }
        super.save(address);
    }

    public void modify(ReceiveAddress receiveAddress, ReceiveAddress address) {
        boolean flag = !receiveAddress.getFirst() && address.getFirst();
        //更新默认地址
        if (flag) {
            ReceiveAddress firstAddress = this.findByFirstAndUserId(receiveAddress.getUserId());
            if (firstAddress != null) {
                firstAddress.setFirst(Boolean.FALSE);
                firstAddress.setUpdateTime(new Date());
                super.update(firstAddress);
            }
        }
        AssertUtils.isTrue(NEED_DEFAULT_ADDRESS, !(receiveAddress.getFirst() && !address.getFirst()));
        BeanUtils.copyProperties(address, receiveAddress, "userId", "id", "createTime", "updateTime");
        receiveAddress.setUpdateTime(new Date());
        super.update(receiveAddress);
    }

    public void remove(ReceiveAddress receiveAddress) {
        super.delete(receiveAddress);
        if (receiveAddress.getFirst()) {
            List<ReceiveAddress> addressList = this.findByUserId(receiveAddress.getUserId());
            if (!CollectionUtils.isEmpty(addressList)) {
                ReceiveAddress setFirstAddress = addressList.get(0);
                setFirstAddress.setFirst(Boolean.TRUE);
                super.update(setFirstAddress);
            }
        }
    }
}
