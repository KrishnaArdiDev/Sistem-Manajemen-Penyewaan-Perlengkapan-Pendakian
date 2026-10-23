package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public interface DapatDicetak {
    
    String cetak();

    default String getJudulCetak() {
        return "NOTA";
    }

    default void tampilkan() {
        System.out.println(getJudulCetak());
        System.out.println(cetak());
    }
}
