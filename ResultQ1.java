package com.example.bajaj.model;

import java.util.List;

public class ResultQ1 {
    private String regNo;
    private List<List<Integer>> outcome;

    public ResultQ1(String regNo, List<List<Integer>> outcome) {
        this.regNo = regNo;
        this.outcome = outcome;
    }

    public String getRegNo() {
        return regNo;
    }

    public List<List<Integer>> getOutcome() {
        return outcome;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setOutcome(List<List<Integer>> outcome) {
        this.outcome = outcome;
    }
}