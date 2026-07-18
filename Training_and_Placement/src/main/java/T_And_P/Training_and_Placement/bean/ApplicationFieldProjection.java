package T_And_P.Training_and_Placement.bean;

import T_And_P.Training_and_Placement.constant.FieldType;

public interface ApplicationFieldProjection {

    Long getFieldId();

    String getFieldName();

    FieldType getFieldType();

    String getStatus();
}