package com.kosa.realestate.admindivision.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * AutoSearchDTO
 *
 * @author 오동건
 */
@Getter
@Setter
public class AutoSearchDTO {

  private Long seq;
  private String name;
  private Double lat;
  private Double lng;
  private Long realEstateId;
  private Double salePrice;
}
