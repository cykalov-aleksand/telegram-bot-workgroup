package pro.sky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statistic {
    @JsonProperty("rule_id")
    private Long ruleId;
    private Integer count;

    public Statistic() {
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "rule_id=" + ruleId +
                ", count=" + count + "; " +
                '}';
    }
}
