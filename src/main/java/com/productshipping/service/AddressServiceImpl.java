package com.productshipping.service;

import com.productshipping.model.AddressEntity;
import com.productshipping.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("AddressService")
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressEntity> getAddresses() {
        return List.of();
    }

    public AddressEntity addAddress(AddressEntity address) {
        address.setAddressId(addressRepository.getNextSeriesId().intValue());
        return addressRepository.save(address);
    }

    @Override
    public AddressEntity updateAddress(AddressEntity addressEntity) {
        return null;
    }

}