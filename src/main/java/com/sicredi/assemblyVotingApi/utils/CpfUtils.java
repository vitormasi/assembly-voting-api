package com.sicredi.assemblyVotingApi.utils;

public class CpfUtils {

    public static String onlyNumbers(String cpf) throws Exception {
        return cpf.replaceAll("[^0-9]", "");
    }

}
