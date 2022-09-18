package com.absence.auth.enums;

public enum LeaveTypeEnum {
    CUTI_TAHUNAN("371c2c6b-7e3c-4575-a8bc-cd28c7362931"),
    CUTI_LINTAS_TAHUN("a125b0c3-5ef6-4e5d-b8a0-b4f47a38d1d9");

    public final String label;

    private LeaveTypeEnum(String label) {
        this.label = label;
    }


}
