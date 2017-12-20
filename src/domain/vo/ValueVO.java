package domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10.12.2017.
 */
public class ValueVO {
    SelectionCriteriaVO primaryKey;
    List otherValues;

    public ValueVO() {
    }

    public SelectionCriteriaVO getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SelectionCriteriaVO primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<SelectionCriteriaVO> getOtherValues() {
        return otherValues == null ? new ArrayList<>() : otherValues;
    }

    public void setOtherValues(List otherValues) {
        this.otherValues = otherValues;
    }
}
