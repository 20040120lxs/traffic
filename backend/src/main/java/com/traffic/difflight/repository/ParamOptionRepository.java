package com.traffic.difflight.repository;

import com.traffic.difflight.entity.ParamOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParamOptionRepository extends JpaRepository<ParamOption, Long> {
    List<ParamOption> findByParamKey(String paramKey);
    void deleteByParamKeyAndParamValue(String paramKey, String paramValue);
}