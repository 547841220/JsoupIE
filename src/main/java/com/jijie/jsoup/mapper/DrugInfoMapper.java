package com.jijie.jsoup.mapper;

import com.jijie.jsoup.entity.DrugInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DrugInfoMapper {

    void insert(DrugInfo drugInfo);

}
