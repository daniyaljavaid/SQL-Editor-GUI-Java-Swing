package Utility;

import java.sql.Types;

public class SQLTypesMapper {
    //this class maps SQL datatypes to Java Datatypes
    public static Class<?> toClass(int type) {
        Class<?> result = Object.class;

        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = String.class;
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;

            case Types.BIT:
                result = Boolean.class;
                break;

            case Types.TINYINT:
                result = Byte.class;
                break;

            case Types.SMALLINT:
                result = Short.class;
                break;

            case Types.INTEGER:
                result = Integer.class;
                break;

            case Types.BIGINT:
                result = Long.class;
                break;

            case Types.REAL:
            case Types.FLOAT:
                result = Float.class;
                break;

            case Types.DOUBLE:
                result = Double.class;
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;

            case Types.DATE:
                result = java.sql.Date.class;
                break;

            case Types.TIME:
                result = java.sql.Time.class;
                break;

            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
        }

        return result;
    }

    public static boolean shouldBeQuoted(String type) {
        if (type.equalsIgnoreCase(String.class.getSimpleName())
                || type.equalsIgnoreCase(java.sql.Date.class.getSimpleName())
                || type.equalsIgnoreCase(java.sql.Timestamp.class.getSimpleName())
                || type.equalsIgnoreCase(java.sql.Time.class.getSimpleName())) {
            return true;
        }
        return false;
    }
}
