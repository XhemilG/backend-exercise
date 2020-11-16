package models.Contents.charts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PieChart extends BasicChart{

    @Override
    public ChartTypes getType() {
        return ChartTypes.Pie;
    }
}
