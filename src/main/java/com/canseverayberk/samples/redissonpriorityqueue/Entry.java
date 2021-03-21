package com.canseverayberk.samples.redissonpriorityqueue;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class Entry implements Comparable<Entry>, Serializable {

    private String value;
    private Integer priority;

    public Entry(String value, Integer priority) {
        this.value = value;
        this.priority = priority;
    }

    @Override
    public int compareTo(Entry o) {
        return o.priority.compareTo(priority);
    }

}
