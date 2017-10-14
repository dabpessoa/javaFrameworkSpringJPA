package me.dabpessoa.framework.util;

import me.dabpessoa.framework.exceptions.ApplicationRuntimeException;

import java.util.Date;

/**
 * Criado por dougllas.sousa em 14/03/2017.
 */
public class UtilsFunctions {

    public static String removeDoisPontos(String string) {
        return string.replaceAll(":", "");
    }

    public static String toString(Object o) {
        return o != null ? o.toString() : null;
    }

    public static String addURLParamsStyleToString1(String string, String name, String value) {
        return addURLParamsStyleToString(string, name, value);
    }

    public static String addURLParamsStyleToString2(String string, String name1, String value1, String name2, String value2) {
        return addURLParamsStyleToString(string, name1, value1, name2, value2);
    }

    public static String addURLParamsStyleToString(String string, String... namesAndValues) {
        if (string == null) return null;
        if (namesAndValues == null || namesAndValues.length == 0) return string;

        StringBuilder builder = new StringBuilder(string);

        if (builder.indexOf("?") == -1) {
            builder.append("?");
        } else builder.append("&");

        if (namesAndValues.length % 2 != 0) throw new ApplicationRuntimeException("A quantidade de parâmetros deve ser par.");

        for(int i = 0 ; i < namesAndValues.length ; i = i + 2) {
            String param = namesAndValues[i];
            String value = namesAndValues[i+1];

            builder.append(param+"="+value);
            if (i+2 != namesAndValues.length) builder.append("&");
        }

        return builder.toString();
    }

    public static String getPeriodoString(Date dataInicial, Date dataFinal) {
        if (dataInicial == null && dataFinal == null) return "Sem Período";
        String dataInicialString = dataInicial != null ? DateUtils.format(dataInicial) : "[sem data inicial]";
        String dataFinalString = dataFinal != null ? DateUtils.format(dataFinal) : "[sem data final]";
        return DateUtils.format(dataInicial) + " a " + DateUtils.format(dataFinal);
    }

}
