package com.htsc.vn.demo.PrisonManagement.model.feign.output;

import lombok.Data;

import java.util.List;

@Data
public class UsersFeignClientOutput {

    private int stt;
    private List<Double> feature;
}
