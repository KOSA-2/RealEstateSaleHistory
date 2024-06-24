package com.kosa.realestate.realestates.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kosa.realestate.realestates.model.RealEstateSale;
import com.kosa.realestate.realestates.model.RealEstateWithSale;

@Mapper
public interface IRealEstateDetailRepository {
  List<RealEstateWithSale> getRealEstateDetail(int salesId);
  List<RealEstateSale> getRealEstatePrice(int salesId);
}
