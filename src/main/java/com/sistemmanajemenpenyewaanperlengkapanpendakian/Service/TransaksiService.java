package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransaksiService {

    private static final List<Transaksi> daftar = new ArrayList<>();

    public static void tambah(Transaksi t) {
        daftar.add(0, t);
    }

    public static List<Transaksi> semua() {
        return Collections.unmodifiableList(daftar);
    }
}
