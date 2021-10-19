package com.jijie.jsoup.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugInfo {


    private int id;

    //priceInfo
    private String detailUrl;
    private String productNameRaw;
    private String brandName;
    private String brandOrGeneric;
    private String formulation;
    private String specification;
    private String specificationPackage;
    private String freeCouponsPrice;
    private String savingClubsPrice;
    private String mailOrderPrice;
    //drugInfo
    private String commonBrands;
    private String type;
    private String pharmacokinetics;
    private String indication;

}
