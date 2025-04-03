/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author Dairon Freddy
 */
public class NumberToLatter {

    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ",
        "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
        "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS",
        "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE"};

    private static final String[] DECENAS = {"VENTI", "TREINTA ", "CUARENTA ",
        "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ",
        "CIEN "};

    private static final String[] CENTENAS = {"CIENTO ", "DOSCIENTOS ",
        "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
        "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    public NumberToLatter() {
    }


    public static String convertNumberToLetter(String number, boolean dolares) throws NumberFormatException {
        return convertNumberToLetter(Double.parseDouble(number), dolares);
    }

    public static String convertNumberToLetter(double doubleNumber, boolean dolares)
            throws NumberFormatException {

        StringBuilder converted = new StringBuilder();

        String patternThreeDecimalPoints = "#.##";

        DecimalFormat format = new DecimalFormat(patternThreeDecimalPoints);
        format.setRoundingMode(RoundingMode.DOWN);

        String formatedDouble = format.format(doubleNumber);
        doubleNumber = Double.parseDouble(formatedDouble.replace(",", "."));

        
        if (doubleNumber < 0) {
            throw new NumberFormatException("El numero debe ser positivo");
        }

        String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
                .split("#");

        int millon = Integer.parseInt(String.valueOf(getDigitByPosition(splitNumber[0],
                8))
                + String.valueOf(getDigitByPosition(splitNumber[0], 7))
                + String.valueOf(getDigitByPosition(splitNumber[0], 6)));
        if (millon == 1) {
            converted.append("UN MILLON ");
        } else if (millon > 1) {
            converted.append(convertNumber(String.valueOf(millon)))
                    .append("MILLONES ");
        }

        int miles = Integer.parseInt(String.valueOf(getDigitByPosition(splitNumber[0],
                5))
                + String.valueOf(getDigitByPosition(splitNumber[0], 4))
                + String.valueOf(getDigitByPosition(splitNumber[0], 3)));
        if (millon >= 1) {
            if (miles == 1) {
                converted.append(convertNumber(String.valueOf(miles)))
                        .append("MIL ");
            } else if (miles > 1) {
                converted.append(convertNumber(String.valueOf(miles)))
                        .append("MIL ");
            }
        } else {
            if (miles == 1) {
                converted.append("UN MIL ");
            }

            if (miles > 1) {
                converted.append(convertNumber(String.valueOf(miles)))
                        .append("MIL ");
            }
        }
        int cientos = Integer.parseInt(String.valueOf(getDigitByPosition(
                splitNumber[0], 2))
                + String.valueOf(getDigitByPosition(splitNumber[0], 1))
                + String.valueOf(getDigitByPosition(splitNumber[0], 0)));
        if (miles >= 1 || millon >= 1) {
            if (cientos >= 1) {
                converted.append(convertNumber(String.valueOf(cientos)));
            }
        } else {
            if (cientos == 1) {
                converted.append("UN ");
            }
            if (cientos > 1) {
                converted.append(convertNumber(String.valueOf(cientos)));
            }
        }

        if (millon + miles + cientos == 0) {
            converted.append("CERO ");
        }

        if (dolares) {
            converted.append(" DOLARES ");
        }

        String valor = splitNumber[1];
        if (dolares) {
            if (valor.length() == 1) {
                converted.append("CON ").append(splitNumber[1]).append("0");
            } else {
                converted.append("CON ").append(splitNumber[1]);
            }

            converted.append("/100 ");
        }
        return converted.toString();
    }

    public static String convertNumber(String number) {

        if (number.length() > 3) {
            throw new NumberFormatException(
                    "La longitud maxima debe ser 3 digitos");
        }

        // Caso especial con el 100
        if (number.equals("100")) {
            return "CIEN ";
        }

        StringBuilder output = new StringBuilder();
        if (getDigitByPosition(number, 2) != 0) {
            output.append(CENTENAS[getDigitByPosition(number, 2) - 1]);
        }

        int k = Integer.parseInt(String.valueOf(getDigitByPosition(number, 1))
                + String.valueOf(getDigitByPosition(number, 0)));

        if (k <= 20) {
            output.append(UNIDADES[k]);
        } else if (k > 30 && getDigitByPosition(number, 0) != 0) {
            output.append(DECENAS[getDigitByPosition(number, 1) - 2])
                    .append("Y ")
                    .append(UNIDADES[getDigitByPosition(number, 0)]);
        } else {
            output.append(DECENAS[getDigitByPosition(number, 1) - 2])
                    .append(UNIDADES[getDigitByPosition(number, 0)]);
        }

        return output.toString();
    }

    private static int getDigitByPosition(String origin, int position) {
        if (origin.length() > position && position >= 0) {
            return origin.charAt(origin.length() - position - 1) - 48;
        }
        return 0;
    }
}
