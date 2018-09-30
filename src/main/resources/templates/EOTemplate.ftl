package ${bussPackage}.${entityPackage}.eo;

#foreach($importClasses in $!{entityImportClasses})
import ${importClasses};
#end

/**
 * ${className}
 */
public class ${className} {
#foreach($po in $!{columnDatas})
#if(${po.shortDataType} == 'Date')
    @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
#end

    /**
     * ${po.columnComment}
     */
    private ${po.shortDataType} ${po.dataName};
#end

    /**
     * java字段名转换为原始数据库列名。<b>如果不存在则返回null</b><br>
     * <p>字段列表：</p>
#foreach($po in $!{columnDatas})
     * <li>${po.dataName} -> ${po.columnName}</li>
#end
     */
    public static String fieldToColumn(String fieldName) {
        if (fieldName == null) return null;
        switch (fieldName) {
#foreach($po in $!{columnDatas})
            case "${po.dataName}": return "${po.columnName}";
#end
            default: return null;
        }
    }

    /**
     * 原始数据库列名转换为java字段名。<b>如果不存在则返回null</b><br>
     * <p>字段列表：</p>
#foreach($po in $!{columnDatas})
     * <li>${po.columnName} -> ${po.dataName}</li>
#end
     */
    public static String columnToField(String columnName) {
        if (columnName == null) return null;
        switch (columnName) {
#foreach($po in $!{columnDatas})
            case "${po.columnName}": return "${po.dataName}";
#end
            default: return null;
        }
    }
#foreach($po in $!{columnDatas})

    /**
     * Get ${po.dataName}
     */
    public ${po.shortDataType} get${po.upperDataName}() {
        return this.${po.dataName};
    }

    /**
     * Set ${po.dataName}
     */
    public void set${po.upperDataName}(${po.shortDataType} ${po.dataName}) {
        this.${po.dataName} = ${po.dataName};
    }
#end
}
