package com.productshipping.service;

import com.productshipping.model.AddressEntity;

import java.util.List;

public interface AddressService {
    List<AddressEntity> getAddresses();
    AddressEntity addAddress(AddressEntity addressEntity);
    AddressEntity updateAddress(AddressEntity addressEntity);
}
