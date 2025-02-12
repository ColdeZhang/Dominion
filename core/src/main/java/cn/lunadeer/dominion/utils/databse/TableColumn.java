package cn.lunadeer.dominion.utils.databse;

import javax.annotation.Nullable;

public class TableColumn extends Field {

    /**
     * 用于 CreateTable
     *
     * @param name            字段名
     * @param type            字段类型
     * @param isPrimary       是否为主键
     * @param isAutoIncrement 是否自增
     * @param isNotNull       是否非空
     * @param isUnique        是否唯一
     * @param defaultValue    默认值
     */
    public TableColumn(String name, FieldType type, boolean isPrimary, boolean isAutoIncrement, boolean isNotNull, boolean isUnique, @Nullable Object defaultValue) {
        super(name, type);
        this.isPrimary = isPrimary;
        this.isAutoIncrement = isAutoIncrement;
        this.isNotNull = isNotNull;
        this.isUnique = isUnique;
        this.defaultValue = defaultValue;
        if (this.isNotNull && this.defaultValue == null) {
            throw new IllegalArgumentException("isNotNull is true, but defaultValue is null");
        }
    }

    /**
     * 用于 AddColumn
     *
     * @param name         字段名
     * @param type         字段类型
     * @param isNotNull    是否非空
     * @param isUnique     是否唯一
     * @param defaultValue 默认值
     */
    public TableColumn(String name, FieldType type, boolean isNotNull, boolean isUnique, Object defaultValue) {
        super(name, type);
        this.isNotNull = isNotNull;
        this.isUnique = isUnique;
        this.defaultValue = defaultValue;
        if (this.isNotNull && this.defaultValue == null) {
            throw new IllegalArgumentException("isNotNull is true, but defaultValue is null");
        }
    }

    public boolean isPrimary;
    public boolean isAutoIncrement;
    public boolean isNotNull;
    public boolean isUnique;
    public Object defaultValue;
}